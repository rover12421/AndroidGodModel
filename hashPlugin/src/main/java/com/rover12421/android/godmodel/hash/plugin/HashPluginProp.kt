package com.rover12421.android.godmodel.hash.plugin

class HashPluginProp {
    var removeHashObjectNameAnnotation: Boolean = true
    var skipConstructor: Boolean = true
    var skipNonPublicField: Boolean = true
    var skipNonPublicMethod: Boolean = true
    var skipMethod: Boolean = false
    var skipField: Boolean = false
    var skipNonStaticField = false
    var skipNonStaticMethod = false

    var murmurHash2IntSeed: Int = 0x9747b28c.toInt()
    var murmurHash2LongSeed: Int = 0xe17a1465.toInt()
    var murmurHash3IntSeed: Int = 104729
    var murmurHash3LongSeed: Int = 104729
}