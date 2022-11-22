package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.Demo
import com.rainbow.pangu.model.vo.DemoVO

object DemoVOConv : Converter<Demo, DemoVO> {
    override fun fromEntity(s: Demo): DemoVO {
        val vo = DemoVO()
        vo.id = s.id
        vo.name = s.name
        return vo
    }
}