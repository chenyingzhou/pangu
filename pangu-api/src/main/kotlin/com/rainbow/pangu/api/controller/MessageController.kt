package com.rainbow.pangu.api.controller

import com.rainbow.pangu.api.model.vo.MessageVO
import com.rainbow.pangu.api.service.MessageService
import com.rainbow.pangu.base.ResultBody
import com.rainbow.pangu.threadholder.ClientInfoHolder
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

@RestController
@Tag(name = "消息")
class MessageController {
    @Resource
    lateinit var messageService: MessageService

    @GetMapping("/message")
    @Operation(summary = "列表")
    fun list(@RequestParam(defaultValue = "1") page: Int): ResultBody<List<MessageVO>> {
        val userId = ClientInfoHolder.userId
        return ResultBody.ok(messageService.list(userId, page))
    }
}
