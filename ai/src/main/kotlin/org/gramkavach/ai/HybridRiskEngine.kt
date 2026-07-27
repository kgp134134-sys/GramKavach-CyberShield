package org.gramkavach.ai

import org.gramkavach.domain.model.PaymentContext
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.domain.model.RiskLevel
import org.gramkavach.domain.model.AnalysisData
import org.gramkavach.domain.model.DetectionFactor
import org.gramkavach.domain.usecase.AssessPaymentRiskUseCase
import javax.inject.Inject

/** Local-first rule layer. Replace the optional ML signal with a vetted ONNX model before release. */
class HybridRiskEngine @Inject constructor() : AssessPaymentRiskUseCase {
    override suspend fun invoke(context: PaymentContext): RiskAssessment {
        val signals = buildList {
            if (context.remoteAccessActive) add("A screen-sharing or remote-access app is active")
            if (context.accessibilityRisk) add("An untrusted app may control accessibility features")
            if (context.unknownCallActive) add("A call from an unknown number is active")
            if (context.collectRequest) add("This is a Collect Request — receiving money never requires paying")
            if (context.suspiciousLinkOpened) add("A suspicious link was opened recently")
            if (context.qrPayment) add("Verify the merchant and amount before scanning a QR code")
        }
        var score = 0
        score += if (context.remoteAccessActive) 50 else 0
        score += if (context.accessibilityRisk) 35 else 0
        score += if (context.unknownCallActive) 25 else 0
        score += if (context.collectRequest) 25 else 0
        score += if (context.suspiciousLinkOpened) 25 else 0
        score += if (context.amountPaise >= 50_000_00) 10 else 0
        score = score.coerceAtMost(100)
        val level = when {
            score >= 80 -> RiskLevel.CRITICAL
            score >= 60 -> RiskLevel.HIGH
            score >= 40 -> RiskLevel.MODERATE
            score >= 15 -> RiskLevel.CAUTION
            else -> RiskLevel.SAFE
        }

        val analysis = when {
            context.remoteAccessActive || context.accessibilityRisk -> {
                AnalysisData(
                    riskSource = "Unknown Caller + Remote Access App",
                    whyThisAlert = "Multiple high-risk indicators suggest a possible social engineering attempt.",
                    recommendedAction = "End the call immediately, close any remote-access app, and never share your OTP or UPI PIN.",
                    factors = listOf(
                        DetectionFactor("Remote Access Detection", context.remoteAccessActive, if (context.remoteAccessActive) 50 else 0),
                        DetectionFactor("Accessibility Vulnerability", context.accessibilityRisk, if (context.accessibilityRisk) 35 else 0),
                        DetectionFactor("Call from Unknown Source", context.unknownCallActive, if (context.unknownCallActive) 25 else 0)
                    )
                )
            }
            context.collectRequest -> {
                AnalysisData(
                    riskSource = "UPI Collect Request",
                    whyThisAlert = "A collect request asks you to authorize a payment. Receiving money never requires entering your UPI PIN.",
                    recommendedAction = "Verify the sender before approving the request. Reject unexpected collect requests.",
                    factors = listOf(
                        DetectionFactor("Collect Request Detected", true, 25),
                        DetectionFactor("External UPI Source", context.unknownCallActive, if (context.unknownCallActive) 25 else 0),
                        DetectionFactor("Phishing Indicators", context.suspiciousLinkOpened, if (context.suspiciousLinkOpened) 25 else 0)
                    )
                )
            }
            context.suspiciousLinkOpened -> {
                AnalysisData(
                    riskSource = "SMS/WhatsApp Link",
                    whyThisAlert = "Suspicious or unverified link detected that may attempt to steal personal or banking information.",
                    recommendedAction = "Do not open the link or enter any sensitive information. Verify the sender first.",
                    factors = listOf(
                        DetectionFactor("Suspicious URL Analysis", true, 25),
                        DetectionFactor("SMS Permission Check", true, 0),
                        DetectionFactor("Blacklisted Domain", true, 0)
                    )
                )
            }
            context.qrPayment -> {
                AnalysisData(
                    riskSource = "External QR Code",
                    whyThisAlert = "The scanned QR code initiates a payment request. Always verify the recipient and amount before entering your UPI PIN.",
                    recommendedAction = "Confirm the merchant and payment details before proceeding.",
                    factors = listOf(
                        DetectionFactor("External QR Scanned", true, 0),
                        DetectionFactor("Merchant Trust Score", true, 0),
                        DetectionFactor("Amount Verification", true, 0)
                    )
                )
            }
            else -> null
        }

        return RiskAssessment(
            score = score,
            level = level,
            reasons = signals.ifEmpty { listOf("No high-risk signals detected") },
            phoneNumber = context.phoneNumber,
            analysis = analysis
        )
    }
}
