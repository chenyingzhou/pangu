package com.rainbow.pangu.repository

import com.rainbow.pangu.entity.Notice
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NoticeRepo : BaseRepo<Notice> {
    fun findAllByType(type: Notice.Type, pageable: Pageable): Page<Notice>
}