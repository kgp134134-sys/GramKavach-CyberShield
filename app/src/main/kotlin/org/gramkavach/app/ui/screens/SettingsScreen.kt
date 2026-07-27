package org.gramkavach.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.gramkavach.app.R
import org.gramkavach.app.SettingsViewModel
import org.gramkavach.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable 
fun Settings(onLanguage: () -> Unit, onAbout: () -> Unit, onGuide: () -> Unit, onProfile: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) { 
    val settings by viewModel.settings.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.settings)) }) }) { padding -> 
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) { 
            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column {
                    ListItem(headlineContent = { Text(stringResource(R.string.profile_title)) }, leadingContent = { Icon(Icons.Default.AccountCircle, null, tint = SaffronDeep) }, modifier = Modifier.clickable { onProfile() })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    ListItem(headlineContent = { Text(stringResource(R.string.app_language)) }, supportingContent = { Text(when(settings.languageTag) { "hi" -> "हिन्दी"; "mr" -> "मराठी"; "bn" -> "বাংলা"; "gu" -> "ગુજરાતી"; "ta" -> "தமிழ்"; "te" -> "తెలుగు"; else -> "English" }) }, leadingContent = { Icon(Icons.Default.Language, null, tint = SaffronDeep) }, modifier = Modifier.clickable { onLanguage() })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    ListItem(headlineContent = { Text(stringResource(R.string.voice_alerts)) }, supportingContent = { Text(stringResource(R.string.voice_alerts_desc)) }, leadingContent = { Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = SaffronDeep) }, trailingContent = { Switch(settings.voiceAlertsEnabled, viewModel::setVoice, colors = SwitchDefaults.colors(checkedThumbColor = SaffronDeep, checkedTrackColor = SaffronDeep.copy(alpha = 0.5f))) })
                }
            }
            
            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column {
                    ListItem(headlineContent = { Text(stringResource(R.string.view_guide)) }, leadingContent = { Icon(Icons.Default.PlayCircle, null, tint = BlueSecondary) }, modifier = Modifier.clickable { onGuide() })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    ListItem(headlineContent = { Text(stringResource(R.string.about_gramkavach)) }, supportingContent = { Text(stringResource(R.string.version)) }, leadingContent = { Icon(Icons.Default.Info, null, tint = SaffronDeep) }, modifier = Modifier.clickable { onAbout() })
                }
            }
        } 
    } 
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable 
fun LanguageSelection(onDone: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) { 
    val languages = listOf("English" to "en", "हिन्दी" to "hi", "বাংলা" to "bn", "मराठी" to "mr", "ગુજરાતી" to "gu", "தமிழ்" to "ta", "తెలుగు" to "te")
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.choose_language)) }) }) { padding -> LazyColumn(Modifier.padding(padding)) { items(languages) { (name, tag) -> ListItem(headlineContent = { Text(name) }, trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }, modifier = Modifier.clickable { viewModel.setLanguage(tag); onDone() }) } } } 
}
