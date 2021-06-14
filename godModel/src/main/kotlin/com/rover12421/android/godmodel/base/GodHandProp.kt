package com.rover12421.android.godmodel.base

open class GodHandProp(val name: String) {
    var type: Class<out GodHandBase> = GodHandBase::class.java
    var runVariant: RunVariant = RunVariant.ALWAYS
    var ignoreJar: Boolean = true
    var filterRegex: String = ""
}