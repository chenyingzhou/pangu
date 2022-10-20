package com.rainbow.pangu.constant

import com.rainbow.pangu.util.DateTimeUtil
import java.time.LocalDateTime

object DefaultValue {
    // MySQL timestamp 最小时间
    val START_TIME: LocalDateTime = DateTimeUtil.parseTimestamp(1L)

    // MySQL timestamp 最大时间
    val END_TIME: LocalDateTime = DateTimeUtil.parseTimestamp(Int.MAX_VALUE.toLong())
}