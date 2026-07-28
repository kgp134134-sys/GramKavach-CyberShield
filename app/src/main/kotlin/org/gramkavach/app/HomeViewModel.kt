package org.gramkavach.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.gramkavach.domain.model.PaymentContext
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.domain.usecase.AssessPaymentRiskUseCase
import org.gramkavach.monitoring.RiskSignalBus

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: AssessPaymentRiskUseCase,
) : ViewModel() {
    private val _assessment = MutableStateFlow<RiskAssessment?>(null)
    val assessment: StateFlow<RiskAssessment?> = _assessment.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun simulateSafe() = viewModelScope.launch {
        _isLoading.value = true
        kotlinx.coroutines.delay(2000)
        runCatching {
            val context = PaymentContext(
                phoneNumber = "+91 99000 11000",
                transactionType = "Verified Payment"
            )
            RiskSignalBus.publish(context)
            useCase(context)
        }.onSuccess { _assessment.value = it }
        _isLoading.value = false
    }

    fun simulatePhishing() = viewModelScope.launch {
        _isLoading.value = true
        kotlinx.coroutines.delay(2000)
        runCatching {
            val context = PaymentContext(
                phoneNumber = "Unknown Sender",
                transactionType = "Phishing Attempt",
                suspiciousLinkOpened = true // Score 25
            )
            RiskSignalBus.publish(context)
            useCase(context)
        }.onSuccess { _assessment.value = it }
        _isLoading.value = false
    }

    fun simulateCollectRequest() = viewModelScope.launch {
        _isLoading.value = true
        kotlinx.coroutines.delay(2000)
        runCatching {
            val context = PaymentContext(
                phoneNumber = "+91 80000 55555",
                transactionType = "UPI Collect Request",
                collectRequest = true,
                unknownCallActive = true // Score 25 + 25 = 50
            )
            RiskSignalBus.publish(context)
            useCase(context)
        }.onSuccess { _assessment.value = it }
        _isLoading.value = false
    }

    fun simulateBankScam() = viewModelScope.launch {
        _isLoading.value = true
        kotlinx.coroutines.delay(2000)
        runCatching {
            val context = PaymentContext(
                phoneNumber = "+91 91111 00000",
                transactionType = "Bank Scam Call",
                remoteAccessActive = true,
                accessibilityRisk = true // Score 50 + 35 = 85
            )
            RiskSignalBus.publish(context)
            useCase(context)
        }.onSuccess { _assessment.value = it }
        _isLoading.value = false
    }

    fun consumeAssessment() { _assessment.value = null }
}
