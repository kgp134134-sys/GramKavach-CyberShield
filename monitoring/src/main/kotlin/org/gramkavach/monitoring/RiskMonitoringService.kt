package org.gramkavach.monitoring

import android.app.ActivityManager
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.gramkavach.core.logging.KavachLogger
import org.gramkavach.ai.HybridRiskEngine
import org.gramkavach.alerts.OverlayController
import org.gramkavach.alerts.RiskNotificationPublisher
import org.gramkavach.domain.model.AlertRecord
import org.gramkavach.domain.repository.RiskRepository

@AndroidEntryPoint
class RiskMonitoringService : LifecycleService() {
    @Inject lateinit var engine: HybridRiskEngine
    @Inject lateinit var repository: RiskRepository
    private var collection: Job? = null
    private var overlay: OverlayController? = null

    override fun onCreate() {
        super.onCreate()
        overlay = OverlayController(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        KavachLogger.i("RiskMonitoringService started")
        if (collection?.isActive != true) collection = lifecycleScope.launch {
            RiskSignalBus.signals.collect { context ->
                runCatching {
                    val risk = engine(context)
                    KavachLogger.d("Risk assessment completed: Score=${risk.score}")
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
                    
                    val isAppForeground = isAppInForeground()
                    // Never show overlay if score is low or if app is in foreground
                    if (android.provider.Settings.canDrawOverlays(this@RiskMonitoringService) && !isAppForeground && risk.score >= 15) {
                        overlay?.show(risk)
                    } else {
                        overlay?.dismiss()
                    }

                    // Always show notification for demo (even for Score 0 / Safe)
                    RiskNotificationPublisher(this@RiskMonitoringService).show(risk)
                }.onFailure { e ->
                    KavachLogger.e("Critical error in risk collection", e)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        return appProcesses.any { it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }

    override fun onDestroy() {
        KavachLogger.i("RiskMonitoringService destroyed")
        collection?.cancel()
        overlay?.dismiss()
        super.onDestroy()
    }
}
