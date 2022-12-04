package com.rainbow.pangu.service

import com.rainbow.pangu.entity.Notice
import com.rainbow.pangu.model.vo.NoticeVO
import com.rainbow.pangu.model.vo.converter.NoticeVOConv
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class NoticeService {
    fun list(type: Notice.Type, page: Int): List<NoticeVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(Notice::weight.name, Notice::createdTime.name).descending())
        val notices = Notice.findAll(mapOf(Notice::type to type), pageable)
        return NoticeVOConv.fromEntity(notices)
    }
}
