package org.gramkavach.monitoring

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import org.gramkavach.domain.model.PaymentContext

/** Opt-in service that observes foreground package names only to flag known remote-control apps. */
class AccessibilityRiskService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString()?.lowercase() ?: return
        if (packageName in REMOTE_CONTROL_PACKAGES) RiskSignalBus.publish(PaymentContext(remoteAccessActive = true, accessibilityRisk = true))
    }
    override fun onInterrupt() = Unit
    private companion object {
        val REMOTE_CONTROL_PACKAGES = setOf("com.teamviewer.quicksupport.market", "com.anydesk.anydeskandroid", "com.microsoft.rdc.androidx", "com.splashtop.remote")
    }
}
