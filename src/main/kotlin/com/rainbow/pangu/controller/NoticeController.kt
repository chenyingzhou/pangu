package com.rainbow.pangu.controller

import com.rainbow.pangu.entity.Notice
import com.rainbow.pangu.model.ResultBody
import com.rainbow.pangu.model.vo.NoticeVO
import com.rainbow.pangu.service.NoticeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import jakarta.annotation.Resource

@RestController
@Tag(name = "公告")
class NoticeController {
    @Resource
    lateinit var noticeService: NoticeService

    @GetMapping("/notice/type/{type}")
    @Operation(summary = "列表")
    fun list(@PathVariable type: Notice.Type, @RequestParam(defaultValue = "1") page: Int): ResultBody<List<NoticeVO>> {
        return ResultBody.ok(noticeService.list(type, page))
    }
}
