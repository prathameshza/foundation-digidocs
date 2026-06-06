package com.pluton.orbitscanner.config

object ProjectConfig {
    // --- APP VERSIONING ---
    // Change these two values when publishing updates to Google Play
    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0.0"

    // --- DYNAMIC SDK CONFIGURATIONS ---
    // Controls SDK target versions globally across all modules
    const val COMPILE_SDK_MAJOR = 36
    const val COMPILE_SDK_MINOR = 1
    const val MIN_SDK = 26
    const val TARGET_SDK = 36

    // --- JVM TOOLCHAIN RESOLUTION ---
    // Aligning with Eclipse Temurin JDK 21 verified in Architecture.md
    const val JDK_VERSION = 21 
}
