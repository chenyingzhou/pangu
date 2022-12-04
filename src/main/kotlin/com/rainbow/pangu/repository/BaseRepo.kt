package com.rainbow.pangu.repository

import com.rainbow.pangu.entity.ActiveRecordEntity
import com.rainbow.pangu.util.AppCtxtUtil
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*
import kotlin.reflect.KClass

interface BaseRepo<T : ActiveRecordEntity> : PagingAndSortingRepository<T, Int>, JpaSpecificationExecutor<T> {

    override fun <S : T> save(entity: S): S

    override fun <S : T> saveAll(entities: Iterable<S>): List<S>

    override fun findById(id: Int): Optional<T>

    override fun existsById(id: Int): Boolean

    override fun findAllById(ids: Iterable<Int>): List<T>

    override fun deleteById(id: Int)

    override fun delete(entity: T)

    override fun deleteAllById(ids: Iterable<Int>)

    override fun deleteAll(entities: Iterable<T>)

    @Deprecated("")
    override fun deleteAll() {
    }

    companion object {
        /**
         * 用于在静态方法中获取Repo
         */
        fun <REPO : BaseRepo<out ActiveRecordEntity>> instance(repoClass: KClass<REPO>): REPO {
            return AppCtxtUtil.getBean(repoClass)
        }
    }
}