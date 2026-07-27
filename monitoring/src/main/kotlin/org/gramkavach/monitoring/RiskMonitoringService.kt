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
import org.gramkavach.domain.model.AlertRecord
import org.gramkavach.domain.repository.RiskRepository
import org.gramkavach.domain.usecase.AssessPaymentRiskUseCase
import org.gramkavach.domain.usecase.RiskNotifier

@AndroidEntryPoint
class RiskMonitoringService : LifecycleService() {
    @Inject lateinit var engine: AssessPaymentRiskUseCase
    @Inject lateinit var repository: RiskRepository
    @Inject lateinit var notifier: RiskNotifier
    private var collection: Job? = null

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
                    // Never show overlay if app is in foreground
                    if (!isAppForeground) {
                        notifier.show(risk)
                    } else {
                        notifier.dismiss()
                    }
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
        notifier.dismiss()
        super.onDestroy()
    }
}
