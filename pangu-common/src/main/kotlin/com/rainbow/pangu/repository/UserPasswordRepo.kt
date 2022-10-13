package com.rainbow.pangu.repository

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.UserPassword
import java.util.*

interface UserPasswordRepo : BaseRepo<UserPassword> {
    fun findByTypeAndUserId(type: UserPassword.Type, userId: Int): Optional<UserPassword>
}