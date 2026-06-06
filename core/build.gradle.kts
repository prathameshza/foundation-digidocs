import com.pluton.orbitscanner.config.ProjectConfig

plugins {
    alias(libs.plugins.android.library)
        alias(libs.plugins.kotlin.compose)
    id("com.android.legacy-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.pluton.orbitscanner.core"
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.androidx.core.ktx)
    // Dagger Hilt Integration
    implementation(libs.google.hilt.android)
    add("kapt", libs.google.hilt.compiler)

    // ==========================================
    // Centralized Storage Dependencies
    // ==========================================
    // Jetpack DataStore (Preferences Storage)
    implementation(libs.androidx.datastore.preferences)

    // Room Database Architecture
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler) // Compiles Room entities via your active KAPT engine
    
    testImplementation(libs.bundles.test.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.android.test.core)
}
