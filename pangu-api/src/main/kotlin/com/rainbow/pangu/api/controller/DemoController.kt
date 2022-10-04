package com.rainbow.pangu.api.controller

import com.rainbow.pangu.api.model.param.DemoParam
import com.rainbow.pangu.api.model.vo.DemoVO
import com.rainbow.pangu.api.service.DemoService
import com.rainbow.pangu.base.ResultBody
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@RestController
@Tag(name = "DEMO")
class DemoController {
    @Resource
    lateinit var demoService: DemoService

    @PostMapping("/demo")
    @Operation(summary = "新增/修改")
    fun save(@RequestBody demoParam: DemoParam): ResultBody<Boolean> {
        return ResultBody.ok(demoService.save(demoParam))
    }

    @GetMapping("/demo")
    @Operation(summary = "列表")
    fun list(): ResultBody<List<DemoVO>> {
        return ResultBody.ok(demoService.list())
    }

    @DeleteMapping("/demo/{id}")
    @Operation(summary = "删除")
    fun delete(@PathVariable id: Int): ResultBody<Boolean> {
        return ResultBody.ok(demoService.delete(id))
    }
}