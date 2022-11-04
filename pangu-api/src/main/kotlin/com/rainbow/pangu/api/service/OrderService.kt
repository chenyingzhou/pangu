package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.OrderItemForMeVO
import com.rainbow.pangu.api.model.vo.OrderItemVO
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.api.model.vo.converter.OrderItemForMeVOConv
import com.rainbow.pangu.api.model.vo.converter.OrderItemVOConv
import com.rainbow.pangu.api.service.payment.PaymentExecutor
import com.rainbow.pangu.entity.BalanceBill
import com.rainbow.pangu.entity.OrderInfo
import com.rainbow.pangu.entity.OrderItem
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.repository.GoodsItemRepo
import com.rainbow.pangu.repository.GoodsRepo
import com.rainbow.pangu.repository.OrderInfoRepo
import com.rainbow.pangu.repository.OrderItemRepo
import com.rainbow.pangu.util.KeyUtil
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.Resource
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class OrderService {
    @Resource
    lateinit var orderInfoRepo: OrderInfoRepo

    @Resource
    lateinit var orderItemRepo: OrderItemRepo

    @Resource
    lateinit var goodsRepo: GoodsRepo

    @Resource
    lateinit var goodsItemRepo: GoodsItemRepo

    @Resource
    lateinit var balanceService: BalanceService

    @Resource
    lateinit var unsoldService: UnsoldService

    @Resource
    lateinit var paymentExecutors: List<PaymentExecutor>

    /**
     * 根据商品创建订单(一级市场)
     */
    fun createByGoodsId(goodsId: Int, count: Int, payParam: PayParam): PaymentOrderUnverifiedVO {
        if (count < 0 || count > 10) {
            throw BizException("一次最多购买10份")
        }
        val now = LocalDateTime.now()
        val goods = goodsRepo.findById(goodsId).orElseThrow()
        if (now < goods.primaryTime || now >= goods.secondaryTime) {
            throw BizException("不在购买时间内")
        }
        val goodsItemIds = unsoldService.pop(goodsId, count)
        if (goodsItemIds.size < count) {
            throw BizException("库存不足，当前库存为${unsoldService.stockNum(goodsId)}")
        }
        try {
            return create(goodsItemIds, payParam)
        } catch (e: Throwable) {
            unsoldService.add(goodsId, goodsItemIds)
            throw e
        }
    }

    /**
     * 根据资产创建订单(二级市场)
     */
    fun createByGoodsItemId(goodsItemId: Int, payParam: PayParam): PaymentOrderUnverifiedVO {
        val goodsItem = goodsItemRepo.findById(goodsItemId).orElseThrow()
        val goods = goodsRepo.findById(goodsItem.goodsId).orElseThrow()
        if (LocalDateTime.now() < goods.secondaryTime) {
            throw BizException("该商品尚未开启二级市场")
        }
        return create(listOf(goodsItemId), payParam)
    }

    /**
     * 生成订单
     */
    private fun create(goodsItemIds: List<Int>, payParam: PayParam): PaymentOrderUnverifiedVO {
        val userId = payParam.userId
        val goodsItems = goodsItemRepo.findAllById(goodsItemIds)
        if (goodsItems.isEmpty()) {
            throw BizException("该资产不存在")
        }
        val goods = goodsRepo.findById(goodsItems[0].goodsId).orElseThrow { BizException("该资产不可售") }
        // 锁定资产
        goodsItems.forEach {
            if (it.userId == userId) {
                throw BizException("不能购买自己的资产")
            }
            if (it.locked || !it.onSale) {
                throw BizException("该资产已被其他人买走")
            }
            it.locked = true
            goodsItemRepo.save(it)
        }
        // 创建订单
        val orderInfo = OrderInfo()
        orderInfo.orderNo = KeyUtil.nextKey
        orderInfo.userId = userId
        orderInfo.goodsId = goods.id
        orderInfo.goodsCount = goodsItems.size
        // 创建订单条目
        val orderItems: MutableList<OrderItem> = ArrayList()
        var totalAmount: Long = 0
        var totalSellerFee: Long = 0
        var totalBuyerFee: Long = 0
        for (goodsItem in goodsItems) {
            val amount: Long = goodsItem.price
            val sellerFee: Long = (amount * goods.sellerFeeRate + 9999) / 10000 // 卖方手续费，ceil(price * sellerFeeRate)
            val buyerFee: Long = (amount * goods.buyerFeeRate + 9999) / 10000 // 买方手续费，ceil(price * buyerFeeRate)
            totalAmount += amount
            totalSellerFee += sellerFee
            totalBuyerFee += buyerFee
            val item = OrderItem()
            item.userId = userId
            item.sellerId = goodsItem.userId
            item.goodsId = goods.id
            item.goodsItemId = goodsItem.id
            item.amount = amount
            item.sellerFee = sellerFee
            item.buyerFee = buyerFee
            orderItems.add(item)
        }
        // 设置
        orderInfo.amount = totalAmount
        orderInfo.sellerFee = totalSellerFee
        orderInfo.buyerFee = totalBuyerFee
        orderInfoRepo.save(orderInfo)
        orderItems.forEach { it.orderId = orderInfo.id }
        orderItemRepo.saveAll(orderItems)
        // 申请支付
        payParam.orderNo = orderInfo.orderNo
        payParam.amount = orderInfo.amount + orderInfo.buyerFee
        val paymentExecutor = paymentExecutors.find { it.type == payParam.paymentMethodType }!!
        val paymentOrderUnverifiedVO = paymentExecutor.apply(payParam)
        // 如果支付状态为成功(余额支付)，直接确认订单状态
        if (paymentOrderUnverifiedVO.status == PaymentOrder.Status.SUCCESS) {
            paid(orderInfo)
        }
        return paymentOrderUnverifiedVO
    }

    /**
     * 订单支付成功
     *
     * @param orderInfo 订单
     */
    private fun paid(orderInfo: OrderInfo) {
        if (orderInfo.paid) { // 已支付的不能重复确认
            return
        }
        // 转移资产
        val userId: Int = orderInfo.userId
        val orderItems = orderItemRepo.findAllByOrderId(orderInfo.id)
        val sellerId: Int = orderItems[0].sellerId
        val goodsItemIds = orderItems.map { it.goodsItemId }
        val goodsItems = goodsItemRepo.findAllById(goodsItemIds)
        // 检查资产是否还在原卖家名下，在：达成交易 不在：交易未达成，后续将退款
        var valid = true
        goodsItems.forEach { valid = valid && it.userId == sellerId }
        if (valid) {
            goodsItems.forEach {
                it.userId = userId
                it.onSale = false
                it.locked = false
                goodsItemRepo.save(it)
            }
        }
        val status = if (valid) OrderInfo.Status.SUCCESS else OrderInfo.Status.FAIL
        orderInfo.paid = true
        orderInfo.status = status
        orderInfoRepo.save(orderInfo)
        orderItems.forEach { it.status = status }
        orderItemRepo.saveAll(orderItems)
        // 给卖家加钱
        if (valid && sellerId > 0) {
            balanceService.add(BalanceBill.Type.SALE, sellerId, orderInfo.amount - orderInfo.sellerFee)
        }
    }

    fun itemList(goodsId: Int, page: Int): List<OrderItemVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(OrderItem::updatedTime.name).descending())
        val items = orderItemRepo.findAllByGoodsIdAndStatusIn(goodsId, listOf(OrderInfo.Status.SUCCESS), pageable)
        return OrderItemVOConv.fromEntity(items)
    }

    fun itemListForMe(userId: Int, page: Int): List<OrderItemForMeVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(OrderItem::updatedTime.name).descending())
        val items = orderItemRepo.findAllByUserAndStatusIn(userId, listOf(OrderInfo.Status.SUCCESS), pageable)
        return OrderItemForMeVOConv.fromEntity(items)
    }
}