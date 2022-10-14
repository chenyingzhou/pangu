package com.rainbow.pangu.api.controller

import com.rainbow.pangu.annotation.LoginCheck
import com.rainbow.pangu.api.model.param.ChangePasswordParam
import com.rainbow.pangu.api.model.param.LoginParam
import com.rainbow.pangu.api.model.param.SendSmsParam
import com.rainbow.pangu.api.model.vo.UserVO
import com.rainbow.pangu.api.service.UserService
import com.rainbow.pangu.base.ResultBody
import com.rainbow.pangu.entity.UserPassword
import com.rainbow.pangu.threadholder.ClientInfoHolder
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

@RestController
@Tag(name = "用户")
class UserController {
    @Resource
    lateinit var userService: UserService

    @PostMapping("/sms/code")
    @Operation(summary = "发送验证码")
    fun sendCode(@RequestBody sendSmsParam: SendSmsParam): ResultBody<Boolean> {
        return ResultBody.ok(userService.sendCode(sendSmsParam.phoneNo))
    }

    @PostMapping("/user/login")
    @Operation(summary = "登录(密码和验证码二选一)")
    fun login(@RequestBody loginParam: LoginParam): ResultBody<String> {
        return ResultBody.ok(userService.login(loginParam.phoneNo, loginParam.password, loginParam.code))
    }

    @PostMapping("/user/info")
    @Operation(summary = "用户信息")
    fun info(@Schema(description = "用户ID,获取自己时可不传") userId: Int?): ResultBody<UserVO> {
        val targetUserId = if (userId != null && userId > 0) userId else ClientInfoHolder.userId
        return ResultBody.ok(userService.info(targetUserId))
    }

    @GetMapping("/user/password")
    @Operation(summary = "密码是否已设置")
    @LoginCheck
    fun hasPassword(type: UserPassword.Type): ResultBody<Boolean> {
        return ResultBody.ok(userService.hasPassword(ClientInfoHolder.userId, type))
    }

    @PostMapping("/user/password")
    @Operation(summary = "修改密码(原密码和验证码二选一)")
    @LoginCheck(lock = true)
    fun changePassword(@RequestBody changePasswordParam: ChangePasswordParam): ResultBody<Boolean> {
        changePasswordParam.userId = ClientInfoHolder.userId
        return ResultBody.ok(userService.changePassword(changePasswordParam))
    }

}