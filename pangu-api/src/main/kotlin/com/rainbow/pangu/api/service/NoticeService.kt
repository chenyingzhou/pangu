package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.vo.NoticeVO
import com.rainbow.pangu.api.model.vo.converter.NoticeVOConv
import com.rainbow.pangu.entity.Notice
import com.rainbow.pangu.repository.NoticeRepo
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class NoticeService {
    @Resource
    lateinit var noticeRepo: NoticeRepo

//    @AsyncCache(timeout = 300)
    fun list(type: Notice.Type, page: Int): List<NoticeVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(Notice::weight.name, Notice::createdTime.name).descending())
        val notices = noticeRepo.findAllByType(type, pageable)
        return NoticeVOConv.fromEntity(notices)
    }
}
