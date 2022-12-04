package com.rainbow.pangu.service

import com.rainbow.pangu.entity.Message
import com.rainbow.pangu.model.vo.MessageVO
import com.rainbow.pangu.model.vo.converter.MessageVOConv
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class MessageService {
    fun list(userId: Int, page: Int): List<MessageVO> {
        if (userId == 0) {
            return listOf()
        }
        val pageable = PageRequest.of(page - 1, 20, Sort.by(Message::createdTime.name).descending())
        val messages = Message.findAll(mapOf(Message::userId to userId), pageable)
        return MessageVOConv.fromEntity(messages)
    }
}
