package com.rainbow.pangu.repository

import com.rainbow.pangu.entity.User
import java.util.*

interface UserRepo : BaseRepo<User> {
    fun findByPhoneNo(phoneNo: String): Optional<User>
}