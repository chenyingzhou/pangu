package com.rainbow.pangu.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateTimeUtil {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun format(localDateTime: LocalDateTime): String {
        return localDateTime.format(dateTimeFormatter)
    }

    fun parse(text: String): LocalDateTime {
        return LocalDateTime.parse(text, dateTimeFormatter)
    }

    fun toTimestamp(localDateTime: LocalDateTime): Long {
        return localDateTime.toEpochSecond(ZoneOffset.ofTotalSeconds(8 * 3600))
    }

    fun parseTimestamp(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofTotalSeconds(8 * 3600))
    }
}
