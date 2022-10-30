package com.rainbow.pangu.repository

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MessageRepo : BaseRepo<Message> {
    fun findAllByUserId(userId: Int, pageable: Pageable): Page<Message>
}