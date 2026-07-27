package org.gramkavach.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.gramkavach.app.R
import org.gramkavach.app.SettingsViewModel
import org.gramkavach.app.ui.theme.*
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.bhashini.ResilientVoiceAlertSpeaker

@Composable
fun RiskAlert(assessment: RiskAssessment?, speaker: ResilientVoiceAlertSpeaker, onDismiss: () -> Unit, settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val result = assessment ?: return
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userSettings by settingsViewModel.settings.collectAsState()
    var voiceError by remember { mutableStateOf<String?>(null) }
    val voiceMessages = mapOf("en" to "Stop. This payment may be risky. Do not enter your UPI PIN.", "hi" to "Rukiye. Yeh payment khatarnak ho sakti hai. Apna UPI PIN share na karein.", "mr" to "Thamba. Ha vyavahar dhokyacha asu shakto. Tumcha UPI PIN sanga nako.", "bn" to "Thambun. Ei lenden bipodjonok hote pare. Tomar UPI PIN share korben na.", "ta" to "Nillu. Intha seluthuthal abayamaga irukkalam. Ungal UPI PIN-ai pagirathirkal.", "te" to "Aagandi. Ee chellimpu pramadakaram kavachhu. Mee UPI PIN-ni panchukovaddu.", "gu" to "થોભો. આ વ્યવહાર જોખમી હોઈ શકે છે. તમારો UPI પિન શેર કરશો નહીં.")
    val currentVoiceMessage = voiceMessages[userSettings.languageTag] ?: voiceMessages["en"]!!
    val color = when { result.score >= 60 -> HighRed; result.score >= 40 -> ModerateOrange; result.score >= 15 -> CautionAmber; else -> SafeGreen }
    
    LaunchedEffect(result.assessedAtEpochMs) { 
        if (userSettings.voiceAlertsEnabled && result.score >= 40) {
            speaker.speak(currentVoiceMessage, userSettings.languageTag) 
        }
    }
    Scaffold { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 96.dp, bottom = 24.dp), 
            verticalArrangement = Arrangement.spacedBy(24.dp), 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(if (result.score >= 40) Icons.Default.Warning else Icons.Default.Shield, contentDescription = null, tint = color, modifier = Modifier.size(80.dp))
            Text(stringResource(R.string.payment_safety_alert), style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)), shape = MaterialTheme.shapes.extraLarge) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.Start) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.risk_score_label), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
                        Text("${result.score}/100", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
                    }
                    HorizontalDivider(color = color.copy(alpha = 0.2f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.risk_level_label), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
                        val levelName = when(result.level) {
                            org.gramkavach.domain.model.RiskLevel.SAFE -> stringResource(R.string.level_safe)
                            org.gramkavach.domain.model.RiskLevel.CAUTION -> stringResource(R.string.level_caution)
                            org.gramkavach.domain.model.RiskLevel.MODERATE -> stringResource(R.string.level_moderate)
                            org.gramkavach.domain.model.RiskLevel.HIGH -> stringResource(R.string.level_high)
                            org.gramkavach.domain.model.RiskLevel.CRITICAL -> stringResource(R.string.level_critical)
                        }
                        Text(levelName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = color)
                    }
                    result.phoneNumber?.let { 
                        HorizontalDivider(color = color.copy(alpha = 0.2f))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            val label = if (result.score == 0) stringResource(R.string.verified_contact) else stringResource(R.string.suspect_label)
                            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
                            Text(it, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold) 
                        }
                    }
                }
            }
            result.analysis?.let { DetectionAnalysisSection(it, color) } ?: run {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)), shape = MaterialTheme.shapes.large) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(stringResource(R.string.risk_detected), fontWeight = FontWeight.Bold); HorizontalDivider(); result.reasons.forEach { reason -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = color); Text(reason, style = MaterialTheme.typography.bodyMedium) } } } }
            }
            Text(stringResource(R.string.alert_footer), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { val shareIntent = Intent().apply { action = Intent.ACTION_SEND; putExtra(Intent.EXTRA_TEXT, "GramKavach Alert: ${result.score}/100 risk detected."); type = "text/plain" }; context.startActivity(Intent.createChooser(shareIntent, "Share")) }, 
                    modifier = Modifier.weight(1f), 
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D47A1)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0D47A1))
                ) { 
                    Icon(Icons.Default.Share, contentDescription = null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.share)) 
                }
                Button(onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, "tel:1930".toUri())) }, colors = ButtonDefaults.buttonColors(containerColor = CriticalRed), modifier = Modifier.weight(1.5f), shape = MaterialTheme.shapes.large) { Icon(Icons.Default.Call, contentDescription = null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.report_1930)) }
            }
            OutlinedButton(
                onClick = { scope.launch { speaker.speak(currentVoiceMessage, userSettings.languageTag).onFailure { voiceError = context.getString(R.string.voice_unavailable) } } }, 
                modifier = Modifier.fillMaxWidth(), 
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D47A1)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0D47A1))
            ) { 
                Text(stringResource(R.string.play_voice_warning)) 
            }
            voiceError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = color)) { Text(stringResource(R.string.i_understand), color = Color.White) }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetectionAnalysisSection(analysis: org.gramkavach.domain.model.AnalysisData, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Detection Analysis Table
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.detection_analysis), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(stringResource(R.string.factor_header), Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold); Text(stringResource(R.string.status_header), Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center); Text(stringResource(R.string.pts_header), Modifier.weight(0.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End) }
                HorizontalDivider()
                analysis.factors.forEach { factor ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(factor.name, Modifier.weight(2f), style = MaterialTheme.typography.bodySmall)
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { val statusColor = if (factor.isDetected) color else MaterialTheme.colorScheme.outline; Surface(color = statusColor.copy(alpha = 0.1f), shape = MaterialTheme.shapes.extraSmall) { Text(if (factor.isDetected) stringResource(R.string.detected) else stringResource(R.string.clear), Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold) } }
                        Text(if (factor.contribution > 0) "+${factor.contribution}" else "0", Modifier.weight(0.5f), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End, fontWeight = if (factor.contribution > 0) FontWeight.Bold else FontWeight.Normal, color = if (factor.contribution > 0) color else MaterialTheme.colorScheme.outline)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
        // 3. Why this alert?
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                Column {
                    Text(stringResource(R.string.why_alert), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                    Text(analysis.whyThisAlert, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color), shape = MaterialTheme.shapes.large) {
            Column(Modifier.padding(16.dp)) { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White); Text(stringResource(R.string.recommended_action), style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(8.dp)); Text(analysis.recommendedAction, style = MaterialTheme.typography.bodyMedium, color = Color.White) }
        }
    }
}
