package com.pluton.orbitscanner.feature.subscription.domain.model

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val priceLabel: String,
    val billingCycleLabel: String,
    val originalPriceLabel: String? = null,
    val discountPercentLabel: String? = null,
    val isBestValue: Boolean = false,
    val originalPriceDetails: String? = null
)
