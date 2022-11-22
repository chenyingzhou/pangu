package com.rainbow.pangu.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import kotlin.reflect.KClass

object JacksonUtil {
    private val threadLocal = ThreadLocal.withInitial {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper
    }

    fun <T : Any> toObject(jsonStr: String, clazz: KClass<T>): T? {
        if (clazz == String::class) {
            return jsonStr as T
        }
        return try {
            threadLocal.get().readValue(jsonStr, clazz.java)
        } catch (e: Exception) {
            null
        }
    }

    fun toObject(jsonStr: String, type: Type): Any? {
        val typeReference = object : TypeReference<Any?>() {
            override fun getType(): Type {
                return type
            }
        }
        return try {
            threadLocal.get().readValue(jsonStr, typeReference)
        } catch (e: Exception) {
            null
        }
    }

    fun <T : Any> toList(jsonStr: String, clazz: KClass<T>): List<T> {
        return try {
            val objectMapper = threadLocal.get()
            val javaType: JavaType = objectMapper.typeFactory.constructParametricType(List::class.java, clazz.java)
            objectMapper.readValue(jsonStr, javaType)
        } catch (e: Exception) {
            listOf()
        }
    }

    fun toJson(obj: Any): String {
        if (obj is String) {
            return obj
        }
        return try {
            threadLocal.get().writeValueAsString(obj)
        } catch (e: Exception) {
            ""
        }
    }
}
