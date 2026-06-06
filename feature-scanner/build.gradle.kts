import com.pluton.orbitscanner.config.ProjectConfig

plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.pluton.orbitscanner.feature.scanner"
    compileSdk {
        version = release(ProjectConfig.COMPILE_SDK_MAJOR) {
            minorApiLevel = ProjectConfig.COMPILE_SDK_MINOR
        }
    }

    defaultConfig {
        minSdk = ProjectConfig.MIN_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.bundles.test.core)
    androidTestImplementation(libs.bundles.android.test.core)
}
