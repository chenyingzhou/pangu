package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.param.DemoParam
import com.rainbow.pangu.api.model.param.converter.DemoParamConv
import com.rainbow.pangu.api.model.vo.DemoVO
import com.rainbow.pangu.api.model.vo.converter.DemoVOConv
import com.rainbow.pangu.repository.DemoRepo
import org.springframework.stereotype.Service
import javax.annotation.Resource
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class DemoService {
    @Resource
    lateinit var demoRepo: DemoRepo

    fun save(param: DemoParam): Boolean {
        val demo = DemoParamConv.toEntity(param)
        demoRepo.save(demo)
        return true
    }

    fun list(): List<DemoVO> {
        val demos = demoRepo.findAll()
        return DemoVOConv.fromEntity(demos)
    }

    fun delete(id: Int): Boolean {
        demoRepo.deleteById(id)
        return true
    }
}
