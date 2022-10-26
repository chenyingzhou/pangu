package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.entity.BalanceBill
import com.rainbow.pangu.entity.OrderInfo
import com.rainbow.pangu.entity.OrderItem
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.repository.GoodsItemRepo
import com.rainbow.pangu.repository.GoodsRepo
import com.rainbow.pangu.repository.OrderInfoRepo
import com.rainbow.pangu.repository.OrderItemRepo
import com.rainbow.pangu.util.KeyUtil
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
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

    /**
     * 生成订单
     */
    fun create(goodsItemIds: List<Int>, payParam: PayParam): PaymentOrderUnverifiedVO {
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
        // 使用余额支付
        if (payParam.paymentMethodType == PaymentMethod.Type.BALANCE) {
            balanceService.add(BalanceBill.Type.PAY, userId, -(orderInfo.amount + orderInfo.buyerFee))
            paid(orderInfo)
            return PaymentOrderUnverifiedVO().apply {
                orderNo = orderInfo.orderNo
            }
        }
        // TODO 使用KFT
        return PaymentOrderUnverifiedVO().apply {
            orderNo = orderInfo.orderNo
            paymentOrderNo = ""
            needSmsValidate = true
        }
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
}