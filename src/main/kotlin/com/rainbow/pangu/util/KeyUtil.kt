package com.rainbow.pangu.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object KeyUtil {
    private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

    /**
     * 生成格式 "2022072112003401000001"
     * 14位表示时间 2位表示发号器序号 6位表示key序号
     */
    val nextKey: String
        get() {
            val now = LocalDateTime.now()
            val number1 = (0..99).random()
            val number2 = (0..999999).random()
            return DATE_TIME_FORMATTER.format(now) + String.format("%02d", number1) + String.format("%06d", number2)
        }
}