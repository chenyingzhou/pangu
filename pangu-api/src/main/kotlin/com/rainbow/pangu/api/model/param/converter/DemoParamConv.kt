package com.rainbow.pangu.api.model.param.converter

import com.rainbow.pangu.api.model.param.DemoParam
import com.rainbow.pangu.entity.Demo

object DemoParamConv : Converter<DemoParam, Demo> {
    override fun toEntity(s: DemoParam): Demo {
        val entity = Demo()
        entity.id = s.id
        entity.name = s.name
        return entity
    }
}