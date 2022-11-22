package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.User
import com.rainbow.pangu.model.vo.UserShortVO

object UserShortVOConv : Converter<User, UserShortVO> {
    override fun fromEntity(s: User): UserShortVO {
        val vo = UserShortVO()
        vo.nickName = s.nickName
        vo.avatar = s.avatar
        vo.signature = s.signature
        return vo
    }
}