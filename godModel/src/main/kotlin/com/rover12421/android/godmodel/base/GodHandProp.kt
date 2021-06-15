package com.rover12421.android.godmodel.base

open class GodHandProp(val name: String) {
    var type: Class<out GodHandBase> = GodHandBase::class.java
    var runVariant: RunVariant = RunVariant.ALWAYS
    var ignoreJar: Boolean = true
    var filterRegex: Regex = "".toRegex()

    // 存储自定义属性, 各插件自己解析
    var other: Any? = null

    fun setFilterRegex(regex: String) {
        filterRegex = regex.toRegex()
    }
}