package com.rover12421.android.godmodel.namehash.base

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Hash(
    vararg val vaules: HashValue
)
