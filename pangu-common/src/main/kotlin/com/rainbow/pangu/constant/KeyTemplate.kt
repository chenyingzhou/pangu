package com.rainbow.pangu.constant

enum class KeyTemplate(private val key: String) {

    ENTITY("entity:{entityName}:{id}"),
    ;

    open fun fill(vararg fields: Any?): String {
        val keyTemplate0 = key.replace(Regex("\\{[a-zA-Z_-]+\\}"), "%s")
        return String.format(keyTemplate0, *fields)
    }

}
