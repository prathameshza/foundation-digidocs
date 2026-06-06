package com.pluton.orbitscanner.feature.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pluton.orbitscanner.feature.subscription.domain.model.SubscriptionPlan
import com.pluton.orbitscanner.feature.subscription.presentation.state.SubscriptionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val mockPlans = listOf(
        SubscriptionPlan(
            id = "yearly_pro",
            name = "Premium Yearly",
            priceLabel = "$39.99",
            billingCycleLabel = "/ year",
            originalPriceLabel = "$79.99",
            discountPercentLabel = "Save 50%",
            isBestValue = true,
            originalPriceDetails = "year\n$79.99"
        ),
        SubscriptionPlan(
            id = "monthly_pro",
            name = "Premium Monthly",
            priceLabel = "$7.99",
            billingCycleLabel = "/ month"
        ),
        SubscriptionPlan(
            id = "weekly_pro",
            name = "Premium Weekly",
            priceLabel = "$2.49",
            billingCycleLabel = "/ week"
        )
    )

    init {
        loadSubscriptionData()
    }

    private fun loadSubscriptionData() {
        viewModelScope.launch {
            delay(300) // Simulate slight network database delay
            _uiState.value = SubscriptionUiState.Success(
                plans = mockPlans,
                selectedPlanId = "yearly_pro"
            )
        }
    }

    fun selectPlan(planId: String) {
        val currentState = _uiState.value
        if (currentState is SubscriptionUiState.Success) {
            _uiState.value = currentState.copy(selectedPlanId = planId)
        }
    }

    fun executePurchase(onCompleted: () -> Unit) {
        val currentState = _uiState.value
        if (currentState is SubscriptionUiState.Success) {
            viewModelScope.launch {
                _uiState.value = currentState.copy(isPurchasing = true)
                delay(1200) // Simulate processing with Play Billing SDK
                _uiState.value = currentState.copy(isPurchasing = false)
                onCompleted()
            }
        }
    }
}
