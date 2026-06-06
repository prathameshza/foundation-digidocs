package com.pluton.orbitscanner.feature.subscription.presentation.state

import com.pluton.orbitscanner.feature.subscription.domain.model.SubscriptionPlan

sealed interface SubscriptionUiState {
    object Loading : SubscriptionUiState
    data class Success(
        val plans: List<SubscriptionPlan>,
        val selectedPlanId: String,
        val isPurchasing: Boolean = false
    ) : SubscriptionUiState
    data class Error(val message: String) : SubscriptionUiState
}
