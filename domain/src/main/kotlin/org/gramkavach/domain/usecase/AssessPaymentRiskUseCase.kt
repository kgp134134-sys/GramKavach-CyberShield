package org.gramkavach.domain.usecase

import org.gramkavach.domain.model.AlertRecord
import org.gramkavach.domain.model.PaymentContext
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.domain.repository.RiskRepository
import javax.inject.Inject

/**
 * Orchestrates the payment risk assessment workflow.
 * 1. Calls the RiskEngine to get an assessment.
 * 2. Saves the result to the repository for history.
 */
class AssessPaymentRiskUseCase @Inject constructor(
    private val engine: RiskEngine,
    private val repository: RiskRepository
) {
    suspend operator fun invoke(context: PaymentContext): RiskAssessment {
        val risk = engine.assess(context)
        
        // Orchestrate saving to history as part of the domain workflow
        repository.saveAlert(
            AlertRecord(
                score = risk.score,
                level = risk.level,
                reasons = risk.reasons,
                phoneNumber = risk.phoneNumber,
                details = context.transactionType,
                createdAtEpochMs = risk.assessedAtEpochMs
            )
        )
        
        return risk
    }
}
