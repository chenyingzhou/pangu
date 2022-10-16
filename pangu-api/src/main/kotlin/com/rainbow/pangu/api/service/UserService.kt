package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.param.ChangePasswordParam
import com.rainbow.pangu.api.model.param.EditUserParam
import com.rainbow.pangu.api.model.vo.LoginVO
import com.rainbow.pangu.api.model.vo.UserVO
import com.rainbow.pangu.api.model.vo.converter.UserVOConv
import com.rainbow.pangu.constant.KeyTemplate
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.entity.UserPassword
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.repository.UserPasswordRepo
import com.rainbow.pangu.repository.UserRepo
import com.rainbow.pangu.threadholder.ClientInfoHolder
import com.rainbow.pangu.util.EnvUtil
import com.rainbow.pangu.util.RedisUtil
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import javax.annotation.Resource
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class UserService {
    @Resource
    lateinit var userRepo: UserRepo

    @Resource
    lateinit var userPasswordRepo: UserPasswordRepo

    /**
     * 发送验证码，返回发送手机号(脱敏处理)
     */
    fun sendCode(userId: Int, phoneNo: String): String {
        var realPhoneNo = phoneNo
        // 若用户ID存在，则使用用户绑定的手机号
        if (userId > 0) {
            realPhoneNo = userRepo.findById(userId).orElseThrow().phoneNo
        }
        if (!realPhoneNo.matches("^1\\d{10}$".toRegex())) {
            throw BizException("请输入正确的手机号")
        }
        val ip = ClientInfoHolder.ip
        val hour = System.currentTimeMillis() / 1000 / 3600
        val limitKey = KeyTemplate.SMS_IP_LIMIT.fill(hour, ip)
        val times = RedisUtil.increment(limitKey)
        RedisUtil.expire(limitKey, 3600)
        if (times != null && times > 10) {
            throw BizException("获取验证码过于频繁，请在1小时重试")
        }
        val code: String = if (EnvUtil.isTest || EnvUtil.isDev) {
            RedisUtil.del(limitKey)
            "888888"
        } else {
            (100000..999999).random().toString()
            // TODO 发送验证码
        }
        RedisUtil.store(KeyTemplate.SMS_CODE.fill(realPhoneNo) to code, 60 * 5)
        return realPhoneNo.substring(0, 3) + "****" + realPhoneNo.substring(7)
    }

    /**
     * 用户登录，可以选择密码或短信验证码
     */
    fun login(phoneNo: String, password: String, code: String, resetPassword: Boolean): LoginVO {
        var user: User? = null
        // 验证码/密码任选其一
        if (code.isBlank() && password.isBlank()) {
            throw BizException("参数不正确")
        }
        // 使用验证码
        if (code.isNotBlank()) {
            val smsCodeKey = KeyTemplate.SMS_CODE.fill(phoneNo)
            val sentCode = RedisUtil.getSingle(smsCodeKey, String::class)
            if (code != sentCode) {
                throw BizException("验证码不正确")
            }
            RedisUtil.del(smsCodeKey)
        }
        // 使用密码
        if (password.isNotBlank()) {
            user = userRepo.findByPhoneNo(phoneNo).orElse(null)
            val userPassword = user?.let {
                userPasswordRepo.findByTypeAndUserId(UserPassword.Type.LOGIN, user!!.id).orElse(null)
            }
            if (userPassword == null || userPassword.password != password) {
                throw BizException("账号或密码不正确")
            }
        }

        // 使用验证码登录时，尚未查询用户，查询之
        if (user == null) {
            user = userRepo.findByPhoneNo(phoneNo).orElse(null)
        }
        // 使用验证码时，该手机号尚未注册，执行注册
        if (user == null) {
            user = User().apply {
                this.phoneNo = phoneNo
                this.nickName = phoneNo.substring(0, 3) + "****" + phoneNo.substring(7)
                this.avatar = "/faces/default.png"
                this.signature = "还未从婴儿车掉落的baby"
            }
            userRepo.save(user)
        }
        // 若选择重置密码，则清除密码
        if (resetPassword) {
            val userPassword = userPasswordRepo.findByTypeAndUserId(UserPassword.Type.LOGIN, user.id).orElse(null)
            if (userPassword != null) {
                userPassword.password = ""
                userPasswordRepo.save(userPassword)
            }
        }
        // 生成TOKEN
        val token = DigestUtils.md5DigestAsHex((user.id.toString() + System.currentTimeMillis()).toByteArray())
        RedisUtil.store(KeyTemplate.USER_TOKEN.fill(token) to user.id, 86400 * 7)
        return LoginVO().apply {
            this.token = token
            this.hasPassword = password.isNotBlank() || hasPassword(user.id, UserPassword.Type.LOGIN)
            this.user = UserVOConv.fromEntity(user)
        }
    }

    /**
     * 获取用户信息
     */
    fun info(userId: Int): UserVO {
        val user = userRepo.findById(userId).orElseGet { User() }
        return UserVOConv.fromEntity(user)
    }

    /**
     * 编辑用户信息
     */
    fun edit(editUserParam: EditUserParam): Boolean {
        val user = userRepo.findById(editUserParam.userId).orElseThrow()
        user.let {
            it.nickName = editUserParam.nickName.ifBlank { it.nickName }
            it.avatar = editUserParam.avatar.ifBlank { it.avatar }
            it.signature = editUserParam.signature.ifBlank { it.signature }
            it.description = editUserParam.description.ifBlank { it.description }
        }
        userRepo.save(user)
        return true
    }

    /**
     * 检查用户是否设置了密码
     */
    fun hasPassword(userId: Int, type: UserPassword.Type): Boolean {
        val userPasswordOpt = userPasswordRepo.findByTypeAndUserId(type, userId)
        return userPasswordOpt.isPresent && userPasswordOpt.get().password.isNotBlank()
    }

    /**
     * 修改密码
     */
    fun changePassword(changePasswordParam: ChangePasswordParam): Boolean {
        val userId = changePasswordParam.userId
        val type = changePasswordParam.type
        val code = changePasswordParam.code
        val oldPassword = changePasswordParam.oldPassword
        val newPassword = changePasswordParam.newPassword

        val userPassword = userPasswordRepo.findByTypeAndUserId(type, userId).orElse(UserPassword())
        // 若原来设置过密码，则需要验证
        if (userPassword.id != 0) {
            if (oldPassword.isNotBlank()) {
                // 使用原密码验证
                if (oldPassword != userPassword.password) {
                    throw BizException("原密码不正确")
                }
            } else {
                // 使用短信验证
                val user = userRepo.findById(userId).orElseThrow()
                val smsCodeKey = KeyTemplate.SMS_CODE.fill(user.phoneNo)
                val sentCode = RedisUtil.getSingle(smsCodeKey, String::class)
                if (code != sentCode) {
                    throw BizException("验证码不正确")
                }
                RedisUtil.del(smsCodeKey)
            }
        }
        userPassword.let {
            it.type = UserPassword.Type.LOGIN
            it.userId = userId
            it.password = newPassword
        }
        userPasswordRepo.save(userPassword)
        return true
    }

    /**
     * 实名认证，仅校验身份证的校验码
     */
    fun realName(userId: Int, realName: String, idCardNo: String): Boolean {
        val user = userRepo.findById(userId).orElseThrow()
        if (user.realNameChecked) {
            throw BizException("不需要重复认证")
        }
        if (realName.length < 2 || realName.length > 10) {
            throw BizException("认证失败")
        }
        if (!checkIdCardNo(idCardNo)) {
            throw BizException("认证失败")
        }
        user.let {
            it.realName = realName
            it.idCardNo = idCardNo
        }
        userRepo.save(user)
        return true
    }

    fun checkIdCardNo(idCardNo: String): Boolean {
        if (idCardNo.length != 18) {
            return false
        }
        val chars = idCardNo.toCharArray()
        chars[17] = if (chars[17] == 'X') '9' + 1 else chars[17]
        val wights = arrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1)
        var sum = 0
        for (i in 0..17) {
            sum += (chars[i] - '0') * wights[i]
        }
        return sum % 11 == 1
    }

}
