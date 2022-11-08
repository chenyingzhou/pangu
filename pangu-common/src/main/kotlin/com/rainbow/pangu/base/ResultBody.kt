package com.rainbow.pangu.base

import com.rainbow.pangu.exception.BizExceptionEnum
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(description = "响应体")
class ResultBody<T> : Serializable {
    @Schema(description = "错误码")
    var code = 0

    @Schema(description = "错误值")
    var biz: BizExceptionEnum? = null

    @Schema(description = "提示信息")
    var msg = ""

    @Schema(description = "数据")
    var data: T? = null

    companion object {
        fun <T> ok(data: T): ResultBody<T> {
            val body = ResultBody<T>()
            body.data = data
            return body
        }

        fun fail(msg: String, bizExceptionEnum: BizExceptionEnum): ResultBody<*> {
            val body: ResultBody<*> = ResultBody<Any>()
            body.msg = msg
            body.biz = bizExceptionEnum
            body.code = bizExceptionEnum.code
            return body
        }
    }
}