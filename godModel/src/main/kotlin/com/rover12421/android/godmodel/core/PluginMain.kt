package com.rover12421.android.godmodel.core

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginMain : Plugin<Project> {
    override fun apply(project: Project) {
        val appExtension = project.extensions.findByType(AppExtension::class.java) ?: return
        appExtension.registerTransform(GodModelTransform(project))
        println(">>>>>>>> godmodel.core PluginMain")
    }
}