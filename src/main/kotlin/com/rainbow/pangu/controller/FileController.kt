package com.rainbow.pangu.controller

import com.rainbow.pangu.enhance.annotation.LoginCheck
import com.rainbow.pangu.service.FileService
import com.rainbow.pangu.model.ResultBody
import com.rainbow.pangu.exception.BizException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import jakarta.annotation.Resource

@RestController
@Tag(name = "文件")
class FileController {
    @Resource
    lateinit var fileService: FileService

    @PostMapping("/upload/image")
    @Operation(summary = "上传图片", description = "支持的的文件格式为jpg，jpeg，gif，png，控制在2M以内")
    @LoginCheck(lock = true)
    fun uploadImage(file: MultipartFile): ResultBody<String> {
        if (file.size > 2 * 1024 * 1024) {
            throw BizException("需控制在2M以内")
        }
        return ResultBody.ok(fileService.upload(file))
    }
}