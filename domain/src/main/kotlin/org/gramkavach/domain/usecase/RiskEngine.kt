package org.gramkavach.domain.usecase

import org.gramkavach.domain.model.PaymentContext
import org.gramkavach.domain.model.RiskAssessment

/** Interface for the underlying risk calculation engine (Rule-based or ML). */
interface RiskEngine {
    suspend fun assess(context: PaymentContext): RiskAssessment
}
