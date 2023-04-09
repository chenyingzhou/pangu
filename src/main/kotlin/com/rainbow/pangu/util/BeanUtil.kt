package com.rainbow.pangu.util

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
object BeanUtil : BeanFactoryAware {

    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    fun <T : Any> getBean(clazz: KClass<T>): T {
        return beanFactory.getBean(clazz.java)
    }

}