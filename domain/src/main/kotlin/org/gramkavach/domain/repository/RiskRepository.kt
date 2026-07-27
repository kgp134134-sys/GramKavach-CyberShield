package org.gramkavach.domain.repository

import kotlinx.coroutines.flow.Flow
import org.gramkavach.domain.model.AlertRecord
import org.gramkavach.domain.model.UserSettings

interface RiskRepository {
    fun observeAlerts(): Flow<List<AlertRecord>>
    suspend fun saveAlert(alert: AlertRecord)
    fun observeSettings(): Flow<UserSettings>
    suspend fun updateSettings(settings: UserSettings)
    suspend fun resetUser()
}
