package com.rover12421.android.godmodel.hash.core

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class IntHashValue(
    val type: Int,
    val value: Int
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class LongHashValue(
    val type: Int,
    val value: Long
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class StringHashValue(
    val type: Int,
    val value: String
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Hash(
    val intHashs: Array<IntHashValue> = [],
    val longHashs: Array<LongHashValue> = [],
    val stringHashs: Array<StringHashValue> = [],
)
