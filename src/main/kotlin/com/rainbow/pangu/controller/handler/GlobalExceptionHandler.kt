package com.rainbow.pangu.controller.handler

import com.rainbow.pangu.model.ResultBody
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.exception.BizExceptionEnum
import com.rainbow.pangu.util.EnvUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerMissParam(e: MethodArgumentNotValidException): ResultBody<*> {
        log.warn(e.message)
        return ResultBody.fail("参数有误", BizExceptionEnum.MISS_PARAM)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handlerMissParam(e: MissingServletRequestParameterException): ResultBody<*> {
        log.warn(e.message)
        return ResultBody.fail("参数不足", BizExceptionEnum.MISS_PARAM)
    }

    @ExceptionHandler(BizException::class)
    fun handlerBizException(e: BizException): ResultBody<*> {
        return ResultBody.fail(e.message ?: "", e.biz)
    }

    @ExceptionHandler(value = [Throwable::class])
    fun exceptionHandler(e: Throwable): ResultBody<*> {
        log.error(e.message, e)
        val message = if (EnvUtil.isProd) "服务器走神了" else e.stackTraceToString()
        return ResultBody.fail(message, BizExceptionEnum.SERVER_ERROR)
    }
}