package com.rainbow.pangu.api.model.vo.converter

interface Converter<S, T> {
    fun fromEntity(s: S): T

    fun fromEntity(ss: Iterable<S>): List<T> {
        return ss.map { fromEntity(it) }
    }
}