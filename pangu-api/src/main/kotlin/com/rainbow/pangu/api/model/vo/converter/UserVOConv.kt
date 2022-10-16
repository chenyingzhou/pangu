package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.UserVO
import com.rainbow.pangu.entity.User

object UserVOConv : Converter<User, UserVO> {
    override fun fromEntity(s: User): UserVO {
        val vo = UserVO()
        vo.id = s.id
        vo.nickName = s.nickName
        vo.avatar = s.avatar
        vo.description = s.description
        vo.creator = s.creator
        vo.signature = s.signature
        vo.hasRealName = s.realName.isNotBlank()
        return vo
    }
}