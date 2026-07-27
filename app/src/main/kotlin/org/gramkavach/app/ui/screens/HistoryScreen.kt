package org.gramkavach.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.gramkavach.app.R
import org.gramkavach.app.HistoryViewModel
import org.gramkavach.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable 
fun AlertHistory(viewModel: HistoryViewModel = hiltViewModel()) {
    val alerts by viewModel.alerts.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.alert_history)) }) }) { padding ->
        if (alerts.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline); Text(stringResource(R.string.no_alerts), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline); Text(stringResource(R.string.history_empty_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline) } }
        } else {
            LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(alerts) { alert ->
                    val alertColor = when { alert.score >= 60 -> HighRed; alert.score >= 40 -> ModerateOrange; alert.score >= 15 -> CautionAmber; else -> SafeGreen }
                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = androidx.compose.foundation.BorderStroke(1.dp, alertColor.copy(alpha = 0.5f))) {
                        Column(Modifier.padding(16.dp).background(alertColor.copy(alpha = 0.05f))) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                val levelName = when(alert.level) {
                                    org.gramkavach.domain.model.RiskLevel.SAFE -> stringResource(R.string.level_safe)
                                    org.gramkavach.domain.model.RiskLevel.CAUTION -> stringResource(R.string.level_caution)
                                    org.gramkavach.domain.model.RiskLevel.MODERATE -> stringResource(R.string.level_moderate)
                                    org.gramkavach.domain.model.RiskLevel.HIGH -> stringResource(R.string.level_high)
                                    org.gramkavach.domain.model.RiskLevel.CRITICAL -> stringResource(R.string.level_critical)
                                }
                                Text(levelName, color = alertColor, fontWeight = FontWeight.Bold); Text(dateFormat.format(Date(alert.createdAtEpochMs)), style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(Modifier.height(4.dp))
                            alert.phoneNumber?.let { val label = if (alert.score == 0) stringResource(R.string.verified_contact) else stringResource(R.string.suspect_label); Text("$label: $it", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                            alert.details?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            Text(alert.reasons.joinToString(", "), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
