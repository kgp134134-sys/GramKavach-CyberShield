package org.gramkavach.alerts

import org.gramkavach.domain.model.RiskAssessment

object AlertMessageFactory {
    fun warning(assessment: RiskAssessment): String = when {
        assessment.score >= 70 -> "Stop. This payment looks risky. Do not enter your UPI PIN."
        assessment.score >= 45 -> "Please verify this payment carefully before entering your UPI PIN."
        else -> "Check the recipient and amount before continuing."
    }
}
