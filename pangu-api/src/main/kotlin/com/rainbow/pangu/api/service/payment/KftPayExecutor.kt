package com.rainbow.pangu.api.service.payment

import com.lycheepay.gateway.client.GBPService
import com.lycheepay.gateway.client.InitiativePayService
import com.lycheepay.gateway.client.dto.gbp.BankIdentifyNoDTO
import com.lycheepay.gateway.client.dto.initiativepay.SmsQuickPayApplyReqDTO
import com.lycheepay.gateway.client.security.KeystoreSignProvider
import com.lycheepay.gateway.client.security.SignProvider
import com.rainbow.pangu.api.config.KftConfig
import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.entity.PaymentAccount
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.repository.PaymentAccountRepo
import com.rainbow.pangu.repository.PaymentOrderRepo
import com.rainbow.pangu.util.JacksonUtil
import com.rainbow.pangu.util.KeyUtil
import com.rainbow.pangu.util.SpringContextUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class KftPayExecutor : PaymentExecutor {
    override val type: PaymentMethod.Type
        get() = PaymentMethod.Type.KFT

    private val keyStorePath = "/usr/local/kft/pfx.pfx"
    private val tempZipDir = "/usr/local/kft/zip"
    private val methodVersion = "1.0.0-PRD"
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    val paymentOrderRepo: PaymentOrderRepo by lazy { SpringContextUtil.getBean(PaymentOrderRepo::class) }
    val paymentAccountRepo: PaymentAccountRepo by lazy { SpringContextUtil.getBean(PaymentAccountRepo::class) }
    val kftConfig: KftConfig by lazy { SpringContextUtil.getBean(KftConfig::class) }
    val signProvider: SignProvider by lazy {
        val keyStorePassword = kftConfig.keyStorePassword.toCharArray()
        val keyPassword = kftConfig.keyPassword.toCharArray()
        KeystoreSignProvider("PKCS12", keyStorePath, keyStorePassword, null, keyPassword)
    }
    val gbpService: GBPService by lazy {
        val clientIp = kftConfig.clientIp
        GBPService(signProvider, clientIp, "zh_CN", tempZipDir)
    }
    val initiativePayService: InitiativePayService by lazy {
        val clientIp = kftConfig.clientIp
        InitiativePayService(signProvider, clientIp, "zh_CN", tempZipDir)
    }

    override fun apply(payParam: PayParam): PaymentOrderUnverifiedVO {
        // 查找或添加支付账号
        val paymentAccount = if (payParam.paymentAccountId > 0) {
            paymentAccountRepo.findById(payParam.paymentAccountId).orElseThrow()
        } else {
            PaymentAccount().apply {
                userId = payParam.userId
                methodType = PaymentMethod.Type.KFT
                accountName = payParam.bankParam!!.accountName
                phoneNo = payParam.bankParam!!.phoneNo
                idCardNo = payParam.bankParam!!.idCardNo
                accountNo = payParam.bankParam!!.accountNo
                bankCode = "UNKNOWN"
                bankName = "银行卡"
                paymentAccountRepo.save(this)
            }
        }
        if (paymentAccount.userId != payParam.userId) {
            throw BizException("银行卡信息错误")
        }
        // 保存支付订单
        val paymentOrder = PaymentOrder().apply {
            paymentOrderNo = KeyUtil.nextKey
            orderNo = payParam.orderNo
            amount = payParam.amount
            status = PaymentOrder.Status.INIT
            type = PaymentMethod.Type.KFT
            accountId = paymentAccount.id
        }
        paymentOrderRepo.save(paymentOrder)
        // 开始申请支付
        val bankNo = if (paymentAccount.bankCode != "UNKNOWN" && paymentAccount.bankCode != "") {
            paymentAccount.bankCode
        } else {
            cardTypeQuery(paymentAccount.accountNo, paymentOrder.paymentOrderNo)
        }
        val dto = SmsQuickPayApplyReqDTO()
        dto.reqNo = paymentOrder.paymentOrderNo // 请求编号
        dto.service = "kpp_sms_collect" // 接口名称，固定不变
        dto.version = methodVersion // 接口版本号，测试:1.0.0-IEST,生产:1.0.0-PRD
        dto.merchantId = kftConfig.proactivePayAccount // 替换成快付通提供的商户ID，测试生产不一样
        dto.secMerchantId = "2022090205227961" // 二级商户id reqNo=1662108436298
        dto.productNo = "1ZD00DXK" // 快付通分配给商户的产品编号（测试和生产环境不同）
        dto.orderNo = paymentOrder.paymentOrderNo // 交易编号
        dto.terminalIp = payParam.ip // APP和网页支付提交用户端ip，主扫支付填调用付API的机器IP
        dto.amount = payParam.amount.toString() // 此次交易的具体金额,单位:分,不支持小数点
        dto.currency = "CNY" // 币种
        dto.tradeName = "短信" // 商品描述,简要概括此次交易的内容.可能会在用户App上显示
        dto.remark = "短信" // 商品详情 可空
        dto.tradeTime = paymentOrder.paymentOrderNo.substring(0, 14) // 商户方交易时间 注意此时间取值一般为商户方系统时间
        dto.custBankNo = bankNo // 支付渠道  参考快付通银行类型参数
        dto.custBankAccountNo = paymentAccount.accountNo // 客户银行账户号 本次交易中,从客户的哪张卡上扣钱
        dto.custBindPhoneNo = paymentAccount.phoneNo // 持卡人开户时绑定手机号，须与相应短信快捷支付申请时一致
        dto.custName = paymentAccount.accountName // 收钱钱的客户的真实姓名
        dto.custBankAcctType = "1" // 客户银行账户类型 1个人 2企业 可空
        dto.custAccountCreditOrDebit = "1" // 客户账户借记贷记类型 1借记 2贷记 3 未知
        dto.custCertificationType = "0" // 收钱客户的身份证件类型 ，参考接口文档
        dto.custID = paymentAccount.idCardNo // 客户证件号码 与上述所选证件类型相匹配的证件号码
        dto.custPhone = paymentAccount.phoneNo // 客户手机号
        dto.isS0 = "0" // 是否是S0支付是否是S0支付，1：是；0：否。默认否。如果是S0支付，金额会实时付给商户。需经快付通审核通过后才可开展此业务
        dto.isGuarantee = "0" // 是否担保交易,1:是，0:否
        dto.isSplit = "0" // 是否分账交易,1:是，0：否 ，
        log.info("短信申请(payNo:${paymentOrder.paymentOrderNo})支付参数:${JacksonUtil.toJson(dto)}")
        val resp = initiativePayService.smsQuickPayApply(dto)
        log.info("短信申请(payNo:${paymentOrder.paymentOrderNo})支付响应:${JacksonUtil.toJson(resp)}")
        // 成功响应结果：短信快捷申请响应：{"reqNo":"1551684624243","status":"7"}
        // 失败响应结果：短信快捷申请响应：{"errorCode":"CASHIER_3136","failureDetails":"信用卡有效期或者CVN2码不能为空","reqNo":"1551684651417","status":"2"}
        val success = resp.status == "7"
        if (!success) {
            throw BizException(resp.failureDetails)
        }
        // 更新银行账户信息
        if (bankNo != paymentAccount.bankCode) {
            paymentAccount.bankCode = bankNo
            paymentAccount.bankName = "TODO银行"
            paymentAccountRepo.save(paymentAccount)
        }

        return PaymentOrderUnverifiedVO().apply {
            status = paymentOrder.status
            needSmsValidate = false
            paymentOrderNo = paymentOrder.paymentOrderNo
            orderNo = paymentOrder.orderNo
        }
    }

    private fun cardTypeQuery(account: String, payNo: String): String {
        val dto = BankIdentifyNoDTO()
        dto.service = "gbp_check_bank_identify_no_info"
        dto.version = methodVersion // 接口版本号，测试:1.0.0-IEST,生产:1.0.0-PRD
        dto.merchantId = kftConfig.proactivePayAccount // 快付通分配给商户的账户编号（测试和生产环境不同）
        dto.productNo = "2DB00ABB" //快付通分配给商户的产品编号（测试和生产环境不同）
        dto.orderNo = payNo // 订单编号必须保证唯一
        dto.cardNo = account // 要查询的银行卡号
        val result = gbpService.queryBankIdentifyNoInfo(dto) // 发送交易请求
        if (result.status != "1") {
            log.error("银行卡信息查询失败：${JacksonUtil.toJson(result)}")
            throw BizException("查询银行卡信息失败!")
        }
        return result.bankType
    }
}