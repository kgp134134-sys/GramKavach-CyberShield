package org.gramkavach.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.gramkavach.domain.repository.RiskRepository

@HiltViewModel class HistoryViewModel @Inject constructor(repository: RiskRepository) : ViewModel() {
    val alerts = repository.observeAlerts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
