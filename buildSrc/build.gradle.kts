plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:9.2.1")
    implementation(kotlin("stdlib-jdk8"))
}

gradlePlugin {
    plugins {
        create("appReleaseVersion") {
            id = "com.digidocx.app.release-version"
            implementationClass = "com.digidocx.plugins.AppReleaseVersionPlugin"
        }
    }
}
