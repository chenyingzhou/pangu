package com.rainbow.pangu.model.vo.converter

interface Converter<S, T> {
    /**
     * entity转vo
     */
    fun fromEntity(s: S): T

    /**
     * 批量转换
     */
    fun fromEntity(ss: Iterable<S>): List<T> {
        prepare(ss)
        return ss.map { fromEntity(it) }
    }

    /**
     * 批量转换的准备工作，一般执行批量查询，由entityCache缓存，以便转换时可直接从缓存获取数据。由实现类重写，若不需要准备工作则不需要重写
     */
    fun prepare(ss: Iterable<S>) {}
}