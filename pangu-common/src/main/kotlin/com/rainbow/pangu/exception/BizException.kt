package com.rainbow.pangu.exception

class BizException : RuntimeException {

    private val bizExceptionEnum: BizExceptionEnum

    val biz
        get() = bizExceptionEnum

    val code
        get() = bizExceptionEnum.code

    constructor(message: String) : super(message) {
        bizExceptionEnum = BizExceptionEnum.FAIL
    }

    constructor(bizExceptionEnum: BizExceptionEnum, message: String) : super(message) {
        this.bizExceptionEnum = bizExceptionEnum
    }

    constructor(message: String, vararg args: Any) : super(String.format(message.replace("{}", "%s"), *args)) {
        this.bizExceptionEnum = BizExceptionEnum.FAIL
    }
}