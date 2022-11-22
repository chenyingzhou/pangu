package com.rainbow.pangu.constant

import com.rainbow.pangu.util.DateTimeUtil
import java.time.LocalDateTime

object DefaultValue {
    // MySQL timestamp 最小时间
    val MIN_TIME: LocalDateTime = DateTimeUtil.parseTimestamp(1L)

    // MySQL timestamp 最大时间
    val MAX_TIME: LocalDateTime = DateTimeUtil.parseTimestamp(Int.MAX_VALUE.toLong())
}