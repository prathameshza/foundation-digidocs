import com.pluton.orbitscanner.config.ProjectConfig

plugins {
    alias(libs.plugins.android.library)
        alias(libs.plugins.kotlin.compose)
    id("com.android.legacy-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.pluton.orbitscanner.feature.home"
    compileSdk {
        version = release(ProjectConfig.COMPILE_SDK_MAJOR) {
            minorApiLevel = ProjectConfig.COMPILE_SDK_MINOR
        }
    }

    defaultConfig {
        minSdk = ProjectConfig.MIN_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(ProjectConfig.JDK_VERSION)
}

dependencies {
    implementation(project(":core"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Dagger Hilt Integration
    implementation(libs.google.hilt.android)
    add("kapt", libs.google.hilt.compiler)

    testImplementation(libs.bundles.test.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.android.test.core)
}
