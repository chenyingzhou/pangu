package com.rainbow.pangu.constant

import com.rainbow.pangu.util.DateTimeUtil
import java.time.LocalDateTime

/**
 * @author bsfeng
 * @since 2022/9/22 11:43
 * @version 1.0
 */
interface SysConstant {
    companion object {
        // MySQL timestamp 最小时间
        val DEFAULT_START_TIME: LocalDateTime = DateTimeUtil.parseTimestamp(1L)
        // MySQL timestamp 最大时间
        val DEFAULT_END_TIME: LocalDateTime = DateTimeUtil.parseTimestamp(Int.MAX_VALUE.toLong())

    }
}
