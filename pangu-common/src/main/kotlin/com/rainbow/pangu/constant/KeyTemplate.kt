package com.rainbow.pangu.constant

enum class KeyTemplate(private val key: String) {
    // 数据
    ENTITY("entity:{entityName}:{id}"),
    SMS_IP_LIMIT("sms:ip:limit:{hour}:{ip}"),
    SMS_CODE("sms:code:{phoneNo}"),
    USER_TOKEN("user:token:{token}"),

    // 分布式锁
    LOCK_BALANCE("balance:userId:{userId}"),
    ;

    open fun fill(vararg fields: Any?): String {
        val keyTemplate0 = key.replace("\\{[a-zA-Z_-]+}".toRegex(), "%s")
        return keyTemplate0.format(*fields)
    }

}
