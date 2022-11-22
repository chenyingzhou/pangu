package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.UserAddress
import com.rainbow.pangu.model.vo.UserAddressVO

object UserAddressVOConv : Converter<UserAddress, UserAddressVO> {
    override fun fromEntity(s: UserAddress): UserAddressVO {
        val vo = UserAddressVO()
        vo.name = s.name
        vo.phoneNo = s.phoneNo
        vo.address = s.address
        return vo
    }
}