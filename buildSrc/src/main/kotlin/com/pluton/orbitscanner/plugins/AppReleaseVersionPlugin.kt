package com.pluton.orbitscanner.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.pluton.orbitscanner.config.ProjectConfig

class AppReleaseVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.android.application") {
            val androidComponents = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            androidComponents.onVariants { variant ->
                variant.outputs.forEach { output ->
                    output.versionCode.set(ProjectConfig.VERSION_CODE)
                    output.versionName.set(ProjectConfig.VERSION_NAME)
                }
            }
        }
    }
}
