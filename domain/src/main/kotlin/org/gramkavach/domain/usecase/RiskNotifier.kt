package org.gramkavach.domain.usecase

import org.gramkavach.domain.model.RiskAssessment

interface RiskNotifier {
    fun show(assessment: RiskAssessment)
    fun dismiss()
}
