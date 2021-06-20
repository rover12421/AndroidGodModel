package com.rover12421.android.godmodel.base

import java.util.*

open class GodHandProp(val name: String) {
    var type: Class<out GodHandBase> = GodHandBase::class.java
    var runVariant: RunVariant = RunVariant.ALWAYS
    var ignoreJar: Boolean = true
    var filterRegex: Regex = "".toRegex()

    // 存储自定义属性, 各插件自己解析
    var property: MutableMap<String, Any> = mutableMapOf()

    fun setFilterRegex(regex: String) {
        filterRegex = regex.toRegex()
    }

    fun getBoolenProp(name: String, default: Boolean): Boolean {
        val prop = property[name] ?: return default
        return prop.toString().toLowerCase(Locale.getDefault()) == "true"
    }

    fun getIntProp(name: String, default: Int): Int {
        val prop = property[name] ?: return default
        val numStr = prop.toString().toLowerCase(Locale.getDefault())
        return if (numStr.startsWith("0x")) {
            numStr.toIntOrNull(16)?:default
        } else {
            numStr.toIntOrNull()?:default
        }
    }

    fun getLongProp(name: String, default: Long): Long {
        val prop = property[name] ?: return default
        val numStr = prop.toString().toLowerCase(Locale.getDefault())
        return if (numStr.startsWith("0x")) {
            numStr.toLongOrNull(16)?:default
        } else {
            numStr.toLongOrNull()?:default
        }
    }
}