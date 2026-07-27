package org.gramkavach.alerts

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.alerts.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiskNotificationPublisher @Inject constructor(@ApplicationContext private val context: Context) {
    @SuppressLint("MissingPermission")
    fun show(risk: RiskAssessment) {
        if ((Build.VERSION.SDK_INT >= 33) && (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)) return
        createChannel()
        val intent = Intent(context, FullScreenWarningActivity::class.java).putExtra(FullScreenWarningActivity.EXTRA_SCORE, risk.score).putStringArrayListExtra(FullScreenWarningActivity.EXTRA_REASONS, ArrayList(risk.reasons))
        val pending = PendingIntent.getActivity(context, risk.assessedAtEpochMs.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val icon = if (risk.score >= 15) android.R.drawable.stat_sys_warning else android.R.drawable.ic_dialog_info
        val title = if (risk.score >= 15) context.getString(R.string.payment_safety_alert) else context.getString(R.string.system_protected)
        val message = if (risk.score >= 15) context.getString(R.string.risk_score_label) + " ${risk.score}/100" else context.getString(R.string.monitoring_active)
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(icon)
            .setContentTitle(title).setContentText(message)
            .setPriority(if (risk.score >= 45) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(if (risk.score >= 45) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(pending).setAutoCancel(true)
            .setFullScreenIntent(pending, risk.score >= 60).build()
        NotificationManagerCompat.from(context).notify(risk.assessedAtEpochMs.toInt(), notification)
    }
    private fun createChannel() { val manager = context.getSystemService(NotificationManager::class.java); manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Payment safety alerts", NotificationManager.IMPORTANCE_HIGH).apply { description = "Urgent GramKavach fraud-risk warnings" }) }
    companion object { const val CHANNEL_ID = "payment_safety_alerts" }
}
