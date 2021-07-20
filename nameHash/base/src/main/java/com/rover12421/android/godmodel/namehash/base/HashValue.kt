package com.rover12421.android.godmodel.namehash.base

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HashValue(
    val len: Int,
    val hashcode: Int,
    val hash: Int,
)
