package com.digidocx.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.api.variant.ApplicationAndroidComponentsExtension

class AppReleaseVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.android.application") {
            val major = 1
            val minor = 0
            val patch = 0
            val buildNumber = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: 1

            val computedVersionCode = (major * 10000) + (minor * 100) + patch + buildNumber
            val computedVersionName = "$major.$minor.$patch"

            val androidComponents = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            androidComponents.onVariants { variant ->
                variant.outputs.forEach { output ->
                    output.versionCode.set(computedVersionCode)
                    output.versionName.set(computedVersionName)
                }
            }
        }
    }
}
