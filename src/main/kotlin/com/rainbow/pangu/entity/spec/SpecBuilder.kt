package com.rainbow.pangu.entity.spec

import com.rainbow.pangu.entity.ActiveRecordEntity
import org.springframework.data.jpa.domain.Specification
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import kotlin.reflect.KProperty1

class SpecBuilder<T : ActiveRecordEntity> {
    private val params: MutableList<Param> = ArrayList()
    private val and: MutableList<SpecBuilder<T>> = ArrayList()
    private val or: MutableList<SpecBuilder<T>> = ArrayList()
    private val selectFields: MutableList<String> = ArrayList()

    fun like(kProperty1: KProperty1<T, Any>, o: Any): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.LIKE, o))
        return this
    }

    fun beginWith(kProperty1: KProperty1<T, Any>, o: Any): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.BEGIN_WITH, o))
        return this
    }

    fun eq(kProperty1: KProperty1<T, Any>, o: Any): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.EQ, o))
        return this
    }

    fun neq(kProperty1: KProperty1<T, Any>, o: Any): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.NEQ, o))
        return this
    }

    fun gt(kProperty1: KProperty1<T, Any>, o: Comparable<*>): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.GT, o))
        return this
    }

    fun gte(kProperty1: KProperty1<T, Any>, o: Comparable<*>): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.GTE, o))
        return this
    }

    fun lt(kProperty1: KProperty1<T, Any>, o: Comparable<*>): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.LT, o))
        return this
    }

    fun lte(kProperty1: KProperty1<T, Any>, o: Comparable<*>): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.LTE, o))
        return this
    }

    fun between(kProperty1: KProperty1<T, Any>, o1: Comparable<*>, o2: Comparable<*>?): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.BETWEEN, o1, o2))
        return this
    }

    fun `in`(kProperty1: KProperty1<T, Any>, o: Collection<*>): SpecBuilder<T> {
        params.add(Param(kProperty1.name, Op.IN, o))
        return this
    }

    fun and(vararg andQuery: SpecBuilder<T>): SpecBuilder<T> {
        and.addAll(andQuery)
        return this
    }

    fun or(vararg orQuery: SpecBuilder<T>): SpecBuilder<T> {
        or.addAll(orQuery)
        return this
    }

    fun select(vararg kProperty1: KProperty1<T, Any>): SpecBuilder<T> {
        selectFields.addAll(kProperty1.map { it.name }.toList())
        return this
    }

    fun build(): Specification<T> {
        val specBuilder = this
        return object : Specification<T> {
            override fun toPredicate(root: Root<T>, query: CriteriaQuery<*>, builder: CriteriaBuilder): Predicate? {
                query.where(*genPredicate(specBuilder, root, builder))
                selectFields.forEach { query.select(root[it]) }
                return query.restriction
            }

            private fun genPredicate(
                specBuilder: SpecBuilder<T>, root: Root<T>, builder: CriteriaBuilder
            ): Array<Predicate> {
                val predicates = ArrayList<Predicate>()
                if (specBuilder.params.isNotEmpty()) {
                    val predicatesArr = specBuilder.params.map { extracted(builder, it, root) }
                    predicates.addAll(predicatesArr)
                }
                if (specBuilder.and.isNotEmpty()) {
                    val and = specBuilder.and.map { genPredicate(it, root, builder) }.flatMap { it.asIterable() }
                    predicates.add(builder.and(*and.toTypedArray()))
                }
                if (specBuilder.or.isNotEmpty()) {
                    val or = specBuilder.or.map { genPredicate(it, root, builder) }.flatMap { it.asIterable() }
                    predicates.add(builder.or(*or.toTypedArray()))
                }
                return predicates.toTypedArray()
            }

            private fun extracted(builder: CriteriaBuilder, param: Param, root: Root<T>): Predicate {
                val path = root.get<Any?>(param.field)
                val o1 = param.o1
                val o2 = param.o2
                return when (param.op) {
                    Op.EQ -> if (o1 == null) builder.isNull(path) else builder.equal(path, o1)
                    Op.NEQ -> if (o1 == null) builder.isNotNull(path) else builder.notEqual(path, o1)
                    Op.IN -> builder.`in`(path).value(o1)
                    Op.LIKE -> builder.like(path.`as`(String::class.java), "%$o1%")
                    Op.BEGIN_WITH -> builder.like(path.`as`(String::class.java), "$o1%")

                    Op.BETWEEN -> builder.between(
                        path.`as`(path.javaType as Class<Comparable<Any>>), o1 as Comparable<Any>, o2 as Comparable<Any>
                    )

                    Op.GT -> builder.greaterThan(
                        path.`as`(path.javaType as Class<Comparable<Any>>), o1 as Comparable<Any>
                    )

                    Op.GTE -> builder.greaterThanOrEqualTo(
                        path.`as`(path.javaType as Class<Comparable<Any>>), o1 as Comparable<Any>
                    )

                    Op.LT -> builder.lessThan(
                        path.`as`(path.javaType as Class<Comparable<Any>>), o1 as Comparable<Any>
                    )

                    Op.LTE -> builder.lessThanOrEqualTo(
                        path.`as`(path.javaType as Class<Comparable<Any>>), o1 as Comparable<Any>
                    )
                }
            }
        }
    }
}
