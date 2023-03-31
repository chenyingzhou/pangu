package com.rainbow.pangu.controller

import com.rainbow.pangu.enhance.annotation.LoginCheck
import com.rainbow.pangu.enhance.threadholder.ClientInfoHolder
import com.rainbow.pangu.entity.UserPassword
import com.rainbow.pangu.model.ResultBody
import com.rainbow.pangu.model.param.*
import com.rainbow.pangu.model.vo.LoginVO
import com.rainbow.pangu.model.vo.UserAddressVO
import com.rainbow.pangu.model.vo.UserVO
import com.rainbow.pangu.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.annotation.Resource

@RestController
@Tag(name = "用户")
class UserController {
    @Resource
    lateinit var userService: UserService

    @PostMapping("/sms/code")
    @Operation(summary = "发送验证码", description = "用于注册、重置密码。在登录状态下，手机号不必传")
    fun sendCode(@RequestBody sendSmsParam: SendSmsParam): ResultBody<String> {
        val userId = ClientInfoHolder.userId
        return ResultBody.ok(userService.sendCode(userId, sendSmsParam.phoneNo))
    }

    @PostMapping("/user/login")
    @Operation(
        summary = "登录",
        description = "未注册的，使用<b>手机号+验证码</b>注册并登录；<br>" +
                "已注册的，使用<b>手机号+密码</b>登录；<br>" +
                "已注册但忘记密码的，使用<b>手机号+验证码+重置标识</b>登录，服务端会清除密码"
    )
    fun login(@RequestBody loginParam: LoginParam): ResultBody<LoginVO> {
        val vo = userService.login(loginParam.phoneNo, loginParam.password, loginParam.code, loginParam.resetPassword)
        return ResultBody.ok(vo)
    }

    @GetMapping("/user/info")
    @Operation(summary = "用户信息")
    fun info(@Schema(description = "用户ID,获取自己时可不传") userId: Int?): ResultBody<UserVO> {
        val targetUserId = if (userId != null && userId > 0) userId else ClientInfoHolder.userId
        return ResultBody.ok(userService.info(targetUserId))
    }

    @PostMapping("/user/info")
    @Operation(summary = "编辑个人信息")
    @LoginCheck(lock = true)
    fun edit(@RequestBody editUserParam: EditUserParam): ResultBody<Boolean> {
        editUserParam.userId = ClientInfoHolder.userId
        return ResultBody.ok(userService.edit(editUserParam))
    }

    @GetMapping("/user/password")
    @Operation(summary = "密码是否已设置")
    @LoginCheck
    fun hasPassword(type: UserPassword.Type): ResultBody<Boolean> {
        return ResultBody.ok(userService.hasPassword(ClientInfoHolder.userId, type))
    }

    @PostMapping("/user/password")
    @Operation(summary = "修改密码", description = "原密码和验证码二选一")
    @LoginCheck(lock = true)
    fun changePassword(@RequestBody changePasswordParam: ChangePasswordParam): ResultBody<Boolean> {
        changePasswordParam.userId = ClientInfoHolder.userId
        return ResultBody.ok(userService.changePassword(changePasswordParam))
    }

    @PostMapping("/user/realName")
    @Operation(summary = "实名认证")
    @LoginCheck(lock = true)
    fun realName(@RequestBody realNameParam: RealNameParam): ResultBody<Boolean> {
        val userId = ClientInfoHolder.userId
        return ResultBody.ok(userService.realName(userId, realNameParam.realName, realNameParam.idCardNo.uppercase()))
    }

    @GetMapping("/user/address")
    @Operation(summary = "获取地址")
    @LoginCheck
    fun getAddress(): ResultBody<UserAddressVO> {
        val userId = ClientInfoHolder.userId
        return ResultBody.ok(userService.getAddress(userId))
    }

    @PostMapping("/user/address")
    @Operation(summary = "修改地址")
    @LoginCheck(lock = true)
    fun setAddress(@RequestBody userAddressParam: UserAddressParam): ResultBody<Boolean> {
        userAddressParam.userId = ClientInfoHolder.userId
        return ResultBody.ok(userService.setAddress(userAddressParam))
    }

}