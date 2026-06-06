package com.pluton.orbitscanner.core.model

data class UserProfile(
    val name: String = "Vishal Sharma",
    val email: String = "vishal.sharma@example.com",
    val isPro: Boolean = false,
    val ocrUsedCount: Int = 3,
    val ocrMaxCount: Int = 5
)