package com.rainbow.pangu.model.param.converter

interface Converter<S, T> {
    fun toEntity(s: S): T

    fun toEntity(ss: Iterable<S>): List<T> {
        return ss.map { toEntity(it) }
    }
}