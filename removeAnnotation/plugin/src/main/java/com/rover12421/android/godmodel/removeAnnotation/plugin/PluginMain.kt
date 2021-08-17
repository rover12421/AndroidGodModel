package com.rover12421.android.godmodel.removeAnnotation.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class PluginMain: Plugin<Project> {
    override fun apply(project: Project) {
        println("Please using godModel plugin load !!")
    }
}