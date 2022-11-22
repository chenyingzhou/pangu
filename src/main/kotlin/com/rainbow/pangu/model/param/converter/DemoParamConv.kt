package com.rainbow.pangu.model.param.converter

import com.rainbow.pangu.entity.Demo
import com.rainbow.pangu.model.param.DemoParam

object DemoParamConv : Converter<DemoParam, Demo> {
    override fun toEntity(s: DemoParam): Demo {
        val entity = Demo()
        entity.id = s.id
        entity.name = s.name
        return entity
    }
}