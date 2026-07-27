package org.gramkavach.domain.model

enum class RiskLevel { SAFE, CAUTION, MODERATE, HIGH, CRITICAL }

data class PaymentContext(
    val phoneNumber: String? = null,
    val transactionType: String? = null,
    val collectRequest: Boolean = false,
    val qrPayment: Boolean = false,
    val remoteAccessActive: Boolean = false,
    val accessibilityRisk: Boolean = false,
    val unknownCallActive: Boolean = false,
    val suspiciousLinkOpened: Boolean = false,
    val amountPaise: Long = 0,
)

data class RiskAssessment(
    val score: Int,
    val level: RiskLevel,
    val reasons: List<String>,
    val phoneNumber: String? = null,
    val analysis: AnalysisData? = null,
    val assessedAtEpochMs: Long = System.currentTimeMillis(),
)

data class DetectionFactor(
    val name: String,
    val isDetected: Boolean,
    val contribution: Int
)

data class AnalysisData(
    val riskSource: String,
    val whyThisAlert: String,
    val recommendedAction: String,
    val factors: List<DetectionFactor>
)

data class AlertRecord(
    val id: Long = 0,
    val score: Int,
    val level: RiskLevel,
    val reasons: List<String>,
    val phoneNumber: String? = null,
    val details: String? = null,
    val createdAtEpochMs: Long,
)

data class UserSettings(
    val languageTag: String = "en",
    val voiceAlertsEnabled: Boolean = true,
    val userName: String = ""
)
