package org.gramkavach.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.gramkavach.core.logging.KavachLogger
import org.gramkavach.data.local.AlertDao
import org.gramkavach.data.local.AlertEntity
import org.gramkavach.domain.model.AlertRecord
import org.gramkavach.domain.model.RiskLevel
import org.gramkavach.domain.model.UserSettings
import org.gramkavach.domain.repository.RiskRepository

private val Context.settingsStore by preferencesDataStore(name = "gramkavach_settings")

class RiskRepositoryImpl(private val alertDao: AlertDao, private val context: Context) : RiskRepository {
    override fun observeAlerts(): Flow<List<AlertRecord>> = alertDao.observeAll()
        .map { rows -> rows.map { it.toDomain() } }
        .catch { e ->
            KavachLogger.e("Error observing alerts", e)
            emit(emptyList<AlertRecord>())
        }

    override suspend fun saveAlert(alert: AlertRecord) {
        runCatching {
            alertDao.insert(
                AlertEntity(
                    score = alert.score.coerceIn(0, 100),
                    level = alert.level.name,
                    reasons = alert.reasons.joinToString("|"),
                    phoneNumber = alert.phoneNumber,
                    details = alert.details,
                    createdAtEpochMs = alert.createdAtEpochMs
                )
            )
            alertDao.cleanupOldAlerts()
        }.onFailure { e ->
            KavachLogger.e("Error saving alert", e)
        }
    }
    override fun observeSettings(): Flow<UserSettings> = context.settingsStore.data.map { preferences ->
        UserSettings(
            languageTag = preferences[LANGUAGE] ?: "en",
            voiceAlertsEnabled = preferences[VOICE] ?: true,
            userName = preferences[USER_NAME] ?: ""
        )
    }
    override suspend fun updateSettings(settings: UserSettings) {
        context.settingsStore.edit { preferences -> 
            preferences[LANGUAGE] = settings.languageTag
            preferences[VOICE] = settings.voiceAlertsEnabled
            preferences[USER_NAME] = settings.userName
        }
    }
    override suspend fun resetUser() {
        context.settingsStore.edit { it.clear() }
        alertDao.deleteAll()
    }
    private fun AlertEntity.toDomain() = AlertRecord(
        id = id,
        score = score.coerceIn(0, 100),
        level = runCatching { RiskLevel.valueOf(level) }.getOrDefault(RiskLevel.CAUTION),
        reasons = reasons.split("|").filter(String::isNotBlank),
        phoneNumber = phoneNumber,
        details = details,
        createdAtEpochMs = createdAtEpochMs
    )
    private companion object { 
        val LANGUAGE = stringPreferencesKey("language_tag")
        val VOICE = booleanPreferencesKey("voice_alerts")
        val USER_NAME = stringPreferencesKey("user_name")
    }
}
