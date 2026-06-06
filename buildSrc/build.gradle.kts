plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:9.2.1")
    // Add the legacy-kapt plugin coordinate to the build classpath
    implementation("com.android.legacy-kapt:com.android.legacy-kapt.gradle.plugin:9.2.1")
    implementation(kotlin("stdlib-jdk8"))
}

gradlePlugin {
    plugins {
        create("appReleaseVersion") {
            id = "com.pluton.orbitscanner.app.release-version"
            implementationClass = "com.pluton.orbitscanner.plugins.AppReleaseVersionPlugin"
        }
    }
}
