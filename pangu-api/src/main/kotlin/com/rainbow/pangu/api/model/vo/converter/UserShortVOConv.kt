package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.UserShortVO
import com.rainbow.pangu.entity.User

object UserShortVOConv : Converter<User, UserShortVO> {
    override fun fromEntity(s: User): UserShortVO {
        val vo = UserShortVO()
        vo.nickName = s.nickName
        vo.avatar = s.avatar
        vo.signature = s.signature
        return vo
    }
}