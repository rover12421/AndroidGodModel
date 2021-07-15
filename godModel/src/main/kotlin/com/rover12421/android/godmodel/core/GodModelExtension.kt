package com.rover12421.android.godmodel.core

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

open class GodModelExtension(project: Project) {

    var runVariant: RunVariant = RunVariant.ALWAYS
    var cacheable: Boolean = true
    var incremental: Boolean = true
    var ignoreJar: Boolean = true
    var parallel: Boolean = true // 并发执行
    var debug: Boolean = false
    var godHands: NamedDomainObjectContainer<GodHandProp> = project.container(GodHandProp::class.java)

    fun godHands(action: Action<NamedDomainObjectContainer<GodHandProp>>) {
        action.execute(godHands)
    }

    companion object {
        const val ExtName = "GodModel"
    }
}