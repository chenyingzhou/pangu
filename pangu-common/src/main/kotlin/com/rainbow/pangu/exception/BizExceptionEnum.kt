package com.rainbow.pangu.exception

enum class BizExceptionEnum(val code: Int) {
    UNAUTHORIZED(-1),
    FAIL(1),
    SERVER_ERROR(2),
    MISS_PARAM(3),
}