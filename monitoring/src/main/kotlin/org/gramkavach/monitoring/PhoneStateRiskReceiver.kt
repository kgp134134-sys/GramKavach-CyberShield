package org.gramkavach.monitoring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import org.gramkavach.domain.model.PaymentContext

class PhoneStateRiskReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING) {
            // Android does not reliably expose a number; an incoming call is treated as a caution signal only.
            RiskSignalBus.publish(PaymentContext(unknownCallActive = true))
        }
    }
}
