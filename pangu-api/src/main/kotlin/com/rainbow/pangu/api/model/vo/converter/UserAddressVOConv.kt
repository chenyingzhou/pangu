package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.UserAddressVO
import com.rainbow.pangu.entity.UserAddress

object UserAddressVOConv : Converter<UserAddress, UserAddressVO> {
    override fun fromEntity(s: UserAddress): UserAddressVO {
        val vo = UserAddressVO()
        vo.name = s.name
        vo.phoneNo = s.phoneNo
        vo.address = s.address
        return vo
    }
}