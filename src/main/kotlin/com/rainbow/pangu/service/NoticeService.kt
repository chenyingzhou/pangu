package com.rainbow.pangu.service

import com.rainbow.pangu.entity.Notice
import com.rainbow.pangu.model.vo.NoticeVO
import com.rainbow.pangu.model.vo.converter.NoticeVOConv
import com.rainbow.pangu.repository.NoticeRepo
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class NoticeService {
    @Resource
    lateinit var noticeRepo: NoticeRepo

    fun list(type: Notice.Type, page: Int): List<NoticeVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(Notice::weight.name, Notice::createdTime.name).descending())
        val notices = noticeRepo.findAllByType(type, pageable)
        return NoticeVOConv.fromEntity(notices)
    }
}
