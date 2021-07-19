package com.rover12421.android.godmodel.core

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginMain : Plugin<Project> {
    override fun apply(project: Project) {
        val aandroidExtension = project.extensions.findByType(AppExtension::class.java)
            ?: project.extensions.findByType(LibraryExtension::class.java) ?: return
        aandroidExtension.registerTransform(GodModelTransform(project))
        println(">>>>>>>> godmodel.core PluginMain")
    }
}