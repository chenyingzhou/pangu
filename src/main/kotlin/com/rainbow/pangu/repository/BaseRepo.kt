package com.rainbow.pangu.repository

import com.rainbow.pangu.enhance.annotation.EntityCache
import com.rainbow.pangu.entity.BaseEntity
import com.rainbow.pangu.util.AppCtxtUtil
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*
import kotlin.reflect.KClass

interface BaseRepo<T : BaseEntity> : PagingAndSortingRepository<T, Int>, JpaSpecificationExecutor<T> {

    @EntityCache
    override fun <S : T> save(entity: S): S

    @EntityCache
    override fun <S : T> saveAll(entities: Iterable<S>): List<S>

    @EntityCache
    override fun findById(id: Int): Optional<T>

    @EntityCache
    override fun existsById(id: Int): Boolean

    @EntityCache
    override fun findAllById(ids: Iterable<Int>): List<T>

    @EntityCache
    override fun deleteById(id: Int)

    @EntityCache
    override fun delete(entity: T)

    @EntityCache
    override fun deleteAllById(ids: Iterable<Int>)

    @EntityCache
    override fun deleteAll(entities: Iterable<T>)

    @Deprecated("")
    override fun deleteAll() {
    }

    companion object {
        /**
         * 用于在静态方法中获取Repo
         */
        fun <REPO : BaseRepo<out BaseEntity>> instance(repoClass: KClass<REPO>): REPO {
            return AppCtxtUtil.getBean(repoClass)
        }
    }
}