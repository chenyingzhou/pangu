package com.rainbow.pangu.controller

import com.rainbow.pangu.enhance.threadholder.ClientInfoHolder
import com.rainbow.pangu.model.ResultBody
import com.rainbow.pangu.model.vo.MessageVO
import com.rainbow.pangu.service.MessageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import jakarta.annotation.Resource

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
