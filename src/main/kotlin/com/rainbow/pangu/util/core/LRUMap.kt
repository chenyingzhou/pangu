package com.rainbow.pangu.util.core

open class LRUMap<K, V>(private var maxElements: Int) : LinkedHashMap<K, V>(maxElements, 0.75f, true) {
    override fun removeEldestEntry(eldest: Map.Entry<K, V>?): Boolean {
        return size > maxElements
    }
}