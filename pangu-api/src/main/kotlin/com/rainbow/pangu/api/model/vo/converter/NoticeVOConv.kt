package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.NoticeVO
import com.rainbow.pangu.entity.Notice

object NoticeVOConv : Converter<Notice, NoticeVO> {
    override fun fromEntity(s: Notice): NoticeVO {
        val vo = NoticeVO()
        vo.type = s.type
        vo.title = s.title
        vo.subTitle = s.subTitle
        vo.previewUrl = s.previewUrl
        vo.contentUrl = s.contentUrl
        return vo
    }
}