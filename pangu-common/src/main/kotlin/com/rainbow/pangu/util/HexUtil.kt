package com.rainbow.pangu.util

import com.rainbow.pangu.exception.BizException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object HexUtil {
    fun toHex(input: String): String {
        val md = try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            throw BizException("系统错误")
        }
        val hash = md.digest(input.toByteArray(StandardCharsets.UTF_8))
        val number = BigInteger(1, hash)
        val hexString = StringBuilder(number.toString(16))
        while (hexString.length < 32) {
            hexString.insert(0, '0')
        }
        return hexString.toString()
    }
}