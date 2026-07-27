package org.gramkavach.alerts

import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.domain.usecase.RiskNotifier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealRiskNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val overlayController: OverlayController,
    private val notificationPublisher: RiskNotificationPublisher
) : RiskNotifier {
    override fun show(assessment: RiskAssessment) {
        // Always show notification
        notificationPublisher.show(assessment)

        // Show overlay only if permission is granted and risk is significant (score >= 15)
        if (Settings.canDrawOverlays(context) && assessment.score >= 15) {
            overlayController.show(assessment)
        } else {
            overlayController.dismiss()
        }
    }

    override fun dismiss() {
        overlayController.dismiss()
    }
}
