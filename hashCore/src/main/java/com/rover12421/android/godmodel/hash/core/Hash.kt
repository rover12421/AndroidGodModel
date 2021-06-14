package com.rover12421.android.godmodel.hash.core

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IntHash(
    vararg val types: IntHashType = [IntHashType.Size, IntHashType.HashCode]
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LongHash(
    vararg val types: LongHashType = [LongHashType.Size, LongHashType.HashCode]
)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class StringHash(
    vararg val types: StringHashType = [StringHashType.Size, StringHashType.HashCode]
)
