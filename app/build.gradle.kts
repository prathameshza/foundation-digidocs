import com.pluton.orbitscanner.config.ProjectConfig

plugins {
    alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.compose)
    id("com.android.legacy-kapt")
    id("dagger.hilt.android.plugin")
    id("com.pluton.orbitscanner.app.release-version")
}

android {
    namespace = "com.pluton.orbitscanner"
    compileSdk {
        version = release(ProjectConfig.COMPILE_SDK_MAJOR) {
            minorApiLevel = ProjectConfig.COMPILE_SDK_MINOR
        }
    }

    defaultConfig {
        applicationId = "com.pluton.orbitscanner"
        minSdk = ProjectConfig.MIN_SDK
        targetSdk = ProjectConfig.TARGET_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    lint {
        abortOnError = false
        checkTestSources = false
    }
}

kotlin {
    jvmToolchain(ProjectConfig.JDK_VERSION)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature-home"))
    implementation(project(":feature-subscription"))
    implementation(project(":feature-scanner"))
    implementation(project(":feature-aiocr"))
    implementation(project(":feature-pdftools"))
    implementation(project(":feature-editor"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.compose.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Dagger Hilt Integration for the App Module
    implementation(libs.google.hilt.android)
    add("kapt", libs.google.hilt.compiler)

    testImplementation(libs.bundles.test.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.bundles.android.test.core)
    debugImplementation(libs.bundles.compose.debug)
}