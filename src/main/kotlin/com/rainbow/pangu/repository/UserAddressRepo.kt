package com.rainbow.pangu.repository

import com.rainbow.pangu.entity.UserAddress
import java.util.*

interface UserAddressRepo : BaseRepo<UserAddress> {
    fun findByUserId(userId: Int): Optional<UserAddress>
}