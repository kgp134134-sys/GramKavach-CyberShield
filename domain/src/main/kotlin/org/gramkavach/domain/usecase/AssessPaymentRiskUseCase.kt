package org.gramkavach.domain.usecase

import org.gramkavach.domain.model.PaymentContext
import org.gramkavach.domain.model.RiskAssessment

fun interface  AssessPaymentRiskUseCase { suspend operator fun invoke(context: PaymentContext): RiskAssessment }
