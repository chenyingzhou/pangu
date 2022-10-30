package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.vo.MessageVO
import com.rainbow.pangu.api.model.vo.converter.MessageVOConv
import com.rainbow.pangu.entity.Message
import com.rainbow.pangu.repository.MessageRepo
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class MessageService {
    @Resource
    lateinit var messageRepo: MessageRepo

    fun list(userId: Int, page: Int): List<MessageVO> {
        if (userId == 0) {
            return listOf()
        }
        val pageable = PageRequest.of(page - 1, 20, Sort.by(Message::createdTime.name).descending())
        val messages = messageRepo.findAllByUserId(userId, pageable)
        return MessageVOConv.fromEntity(messages)
    }
}
