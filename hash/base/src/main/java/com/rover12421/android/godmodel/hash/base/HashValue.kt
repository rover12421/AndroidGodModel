package com.rover12421.android.godmodel.hash.base

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IntHashValue(
    val type: IntHashType,
    val value: Int
)

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LongHashValue(
    val type: LongHashType,
    val value: Long
)

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class StringHashValue(
    val type: StringHashType,
    val value: String
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Hash(
    val intHash: Array<IntHashValue> = [],
    val longHash: Array<LongHashValue> = [],
    val stringHash: Array<StringHashValue> = [],
)
