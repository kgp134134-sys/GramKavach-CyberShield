package org.gramkavach.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.gramkavach.domain.model.UserSettings
import org.gramkavach.domain.repository.RiskRepository

@HiltViewModel class SettingsViewModel @Inject constructor(private val repository: RiskRepository) : ViewModel() {
    val settings = repository.observeSettings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())
    fun setLanguage(tag: String) = viewModelScope.launch { repository.updateSettings(settings.value.copy(languageTag = tag)) }
    fun setVoice(enabled: Boolean) = viewModelScope.launch { repository.updateSettings(settings.value.copy(voiceAlertsEnabled = enabled)) }
}
