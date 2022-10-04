package com.rainbow.pangu.api.model.param.converter

interface Converter<S, T> {
    fun toEntity(s: S): T

    fun toEntity(ss: Iterable<S>): List<T> {
        return ss.map { toEntity(it) }
    }
}