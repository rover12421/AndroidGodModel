package com.rover12421.android.godmodel.hash.base

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IntHash(
    vararg val types: IntHashType
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LongHash(
    vararg val types: LongHashType
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class StringHash(
    vararg val types: StringHashType
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class HashObjectName(
    val name: String
)