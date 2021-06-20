package com.rover12421.android.godmodel.hash.core

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HashProp(
    val removeHashObjectNameAnnotation: Boolean, // default true
    val skipConstructor: Boolean, // default true
    val skipNonPublicField: Boolean, // default true
    val skipNonPublicMethod: Boolean, // default true
    val skipMethod: Boolean, // default true
    val skipField: Boolean, // default false
    val skipNonStaticField: Boolean, // default false
    val skipNonStaticMethod: Boolean, // default false
)