package com.rainbow.pangu.util

import com.rainbow.pangu.constant.RedisConstant
import com.rainbow.pangu.threadholder.ClientInfoHolder
import org.springframework.util.DigestUtils

object TokenUtil {
    fun login(userId: Int): String {
        val tokenParam = "" + userId + System.currentTimeMillis()
        val token = DigestUtils.md5DigestAsHex(tokenParam.toByteArray())
        RedisUtil.store(RedisConstant.TOKEN + token to userId, RedisConstant.EXPIRE_ONE_WEEK.toLong())
        return token
    }

    fun logout() {
        val token = ClientInfoHolder.token
        if (token != "") {
            RedisUtil.del(RedisConstant.TOKEN + token)
        }
    }
}
