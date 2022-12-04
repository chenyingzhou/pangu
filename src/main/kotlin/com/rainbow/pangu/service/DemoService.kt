package com.rainbow.pangu.service

import com.rainbow.pangu.entity.Demo
import com.rainbow.pangu.model.param.DemoParam
import com.rainbow.pangu.model.param.converter.DemoParamConv
import com.rainbow.pangu.model.vo.DemoVO
import com.rainbow.pangu.model.vo.converter.DemoVOConv
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class DemoService {
    fun save(param: DemoParam): Boolean {
        val demo = DemoParamConv.toEntity(param)
        demo.save()
        return true
    }

    fun list(): List<DemoVO> {
        val demos = Demo.findAll()
        return DemoVOConv.fromEntity(demos)
    }

    fun delete(id: Int): Boolean {
        Demo.deleteById(id)
        return true
    }
}
