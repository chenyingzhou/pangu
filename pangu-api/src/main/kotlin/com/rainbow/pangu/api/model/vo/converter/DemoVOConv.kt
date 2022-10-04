package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.DemoVO
import com.rainbow.pangu.entity.Demo

object DemoVOConv : Converter<Demo, DemoVO> {
    override fun fromEntity(s: Demo): DemoVO {
        val vo = DemoVO()
        vo.id = s.id
        vo.name = s.name
        return vo
    }
}