@file:OptIn(ExperimentalMaterial3Api::class)

package org.gramkavach.app

import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings as AndroidSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.gramkavach.app.ui.theme.*
import org.gramkavach.bhashini.BhashiniTextToSpeech
import org.gramkavach.bhashini.ResilientVoiceAlertSpeaker
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.monitoring.RiskMonitoringService
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.seconds
import androidx.appcompat.app.AppCompatDelegate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { startMonitoring() }
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState)
        requestMonitoringPermissions()
        setContent { GramKavachApp() } 
    }
    private fun requestMonitoringPermissions() {
        val requested = buildList { 
            add(Manifest.permission.READ_PHONE_STATE)
            if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS) 
        }
        if (requested.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            permissions.launch(requested.toTypedArray()) 
        } else {
            startMonitoring()
        }
    }
    private fun startMonitoring() { 
        startService(Intent(this, RiskMonitoringService::class.java)) 
    }
}

@Composable
private fun GramKavachApp() {
    val nav = rememberNavController()
    val context = LocalContext.current
    var assessment by remember { mutableStateOf<RiskAssessment?>(null) }
    
    val speaker = remember { 
        ResilientVoiceAlertSpeaker(context, BhashiniTextToSpeech(context, BuildConfig.BHASHINI_BASE_URL, BuildConfig.BHASHINI_USER_ID, BuildConfig.BHASHINI_API_KEY))
    }

    GramKavachTheme {
        val userSettings by hiltViewModel<SettingsViewModel>().settings.collectAsState()
        LaunchedEffect(userSettings.languageTag) {
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(userSettings.languageTag)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
        NavHost(nav, startDestination = "splash") {
            composable("splash") { Splash { nav.navigate("home") { popUpTo("splash") { inclusive = true } } } }
            composable("home") { Home(onAssess = { assessment = it; nav.navigate("risk") }, onOpen = nav::navigate) }
            composable("risk") { RiskAlert(assessment, speaker, onDismiss = { nav.popBackStack() }) }
            composable("history") { AlertHistory() }
            composable("guide") { UserGuide { nav.popBackStack() } }
            composable("settings") { Settings(onLanguage = { nav.navigate("language") }, onAbout = { nav.navigate("about") }, onGuide = { nav.navigate("guide") }) }
            composable("language") { LanguageSelection(onDone = { nav.popBackStack() }) }
            composable("about") { About { nav.popBackStack() } }
        }
    }
}

@Composable 
private fun Splash(onReady: () -> Unit) { 
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(1.5.seconds); onReady() }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(160.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displayMedium, color = BluePrimary, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
            Text(stringResource(R.string.tagline), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } 
}

@Composable
private fun RiskGauge(score: Int, modifier: Modifier = Modifier) {
    val color by animateColorAsState(targetValue = when { score >= 60 -> HighRed; score >= 40 -> ModerateOrange; score >= 15 -> CautionAmber; else -> SafeGreen }, label = "GaugeColor")
    val animatedScore by animateIntAsState(targetValue = score, label = "ScoreValue")
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawArc(color = Color.LightGray.copy(alpha = 0.2f), startAngle = 140f, sweepAngle = 260f, useCenter = false, style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round))
            drawArc(color = color, startAngle = 140f, sweepAngle = (animatedScore / 100f) * 260f, useCenter = false, style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round))
        }
        Box(Modifier.size(200.dp)) {
            Text("0", Modifier.align(Alignment.BottomStart).padding(start = 32.dp, bottom = 32.dp), style = MaterialTheme.typography.labelSmall)
            Text("100", Modifier.align(Alignment.BottomEnd).padding(end = 32.dp, bottom = 32.dp), style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = animatedScore.toString(), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = color)
            Text(text = stringResource(R.string.risk_score_label), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable 
private fun Home(onAssess: (RiskAssessment) -> Unit, onOpen: (String) -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val assessment by viewModel.assessment.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val alerts by hiltViewModel<HistoryViewModel>().alerts.collectAsState()
    var hasOverlayPermission by remember { mutableStateOf(AndroidSettings.canDrawOverlays(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) hasOverlayPermission = AndroidSettings.canDrawOverlays(context) }
        lifecycleOwner.lifecycle.addObserver(observer); onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(assessment) { assessment?.let { onAssess(it); viewModel.consumeAssessment() } }

    Scaffold(topBar = { TopAppBar(title = { Text("🛡️ " + stringResource(R.string.app_name), fontWeight = FontWeight.Bold) }, actions = { IconButton(onClick = { onOpen("settings") }) { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) } }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val latestScore = if (alerts.isNotEmpty()) alerts.first().score else 0
                val currentStatusColor = when { latestScore >= 60 -> HighRed; latestScore >= 40 -> ModerateOrange; latestScore >= 15 -> CautionAmber; else -> SafeGreen }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        RiskGauge(score = latestScore)
                        Spacer(Modifier.height(16.dp))
                        val status = when { 
                            latestScore >= 60 -> stringResource(R.string.risk_detected) + ": " + stringResource(R.string.high_risk_badge)
                            latestScore >= 40 -> stringResource(R.string.risk_detected) + " ⚠️"
                            latestScore > 0 -> stringResource(R.string.risk_detected) + " ⚠️"
                            hasOverlayPermission -> stringResource(R.string.system_protected) + " ✅"
                            else -> stringResource(R.string.setup_required) + " ⚠️" 
                        }
                        Text(
                            text = status, 
                            style = MaterialTheme.typography.titleLarge, 
                            textAlign = TextAlign.Center,
                            color = if (latestScore > 0) currentStatusColor else if (hasOverlayPermission) SafeGreen else HighRed, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                AnimatedVisibility(visible = hasOverlayPermission) {
                    val badgeColor = if (latestScore > 0) currentStatusColor else SafeGreen
                    Surface(color = badgeColor.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium, border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(if (latestScore > 0) Icons.Default.Warning else Icons.Default.CheckCircle, contentDescription = null, tint = badgeColor, modifier = Modifier.size(16.dp))
                            Text(if (latestScore > 0) "Risk Monitoring Active" else stringResource(R.string.monitoring_active), style = MaterialTheme.typography.labelMedium, color = badgeColor)
                        }
                    }
                }
                Text(stringResource(R.string.how_it_works), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onOpen("guide") },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.view_guide), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.about_desc), style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                Text(stringResource(R.string.quick_actions), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.weight(1f).clickable { onOpen("history") }, shape = MaterialTheme.shapes.large) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary); Text(stringResource(R.string.history), style = MaterialTheme.typography.titleSmall); Text(stringResource(R.string.alerts_count, alerts.size), style = MaterialTheme.typography.bodySmall) } }
                    Card(modifier = Modifier.weight(1f).clickable { context.startActivity(Intent(Intent.ACTION_DIAL, "tel:1930".toUri())) }, shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.error); Text(stringResource(R.string.help_1930), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error); Text(stringResource(R.string.report_fraud), style = MaterialTheme.typography.bodySmall) } }
                }
                AnimatedVisibility(visible = !hasOverlayPermission) { Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = MaterialTheme.shapes.large) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) { Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error); Column(Modifier.weight(1f)) { Text(stringResource(R.string.overlay_disabled), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold); Text(stringResource(R.string.overlay_required), style = MaterialTheme.typography.bodySmall) }; Button(onClick = { context.startActivity(Intent(AndroidSettings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:${context.packageName}".toUri())) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text(stringResource(R.string.enable)) } } } }
                Text(stringResource(R.string.hackathon_demo), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = viewModel::simulateSafe, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SafeGreen), enabled = !isLoading) { Text("1. Simulate Safe (Score 0)") }
                        Button(onClick = viewModel::simulatePhishing, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CautionAmber), enabled = !isLoading) { Text("2. Simulate Phishing (Score 25)", color = Color.Black) }
                        Button(onClick = viewModel::simulateCollectRequest, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ModerateOrange), enabled = !isLoading) { Text("3. Simulate UPI Collect (Score 50)") }
                        Button(onClick = viewModel::simulateBankScam, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = HighRed), enabled = !isLoading) { Text("4. Simulate Bank Scam (Score 85)") }
                    }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

@Composable
private fun DetectionAnalysisSection(analysis: org.gramkavach.domain.model.AnalysisData, color: Color) {
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

@Composable
private fun RiskAlert(assessment: RiskAssessment?, speaker: ResilientVoiceAlertSpeaker, onDismiss: () -> Unit, settingsViewModel: SettingsViewModel = hiltViewModel()) {
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
                OutlinedButton(onClick = { val shareIntent = Intent().apply { action = Intent.ACTION_SEND; putExtra(Intent.EXTRA_TEXT, "GramKavach Alert: ${result.score}/100 risk detected."); type = "text/plain" }; context.startActivity(Intent.createChooser(shareIntent, "Share")) }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.large) { Icon(Icons.Default.Share, contentDescription = null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.share)) }
                Button(onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, "tel:1930".toUri())) }, colors = ButtonDefaults.buttonColors(containerColor = CriticalRed), modifier = Modifier.weight(1.5f), shape = MaterialTheme.shapes.large) { Icon(Icons.Default.Call, contentDescription = null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.report_1930)) }
            }
            OutlinedButton(onClick = { scope.launch { speaker.speak(currentVoiceMessage, userSettings.languageTag).onFailure { voiceError = context.getString(R.string.voice_unavailable) } } }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) { Text(stringResource(R.string.play_voice_warning)) }
            voiceError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = color)) { Text(stringResource(R.string.i_understand), color = Color.White) }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UserGuide(onDone: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    val totalSteps = 4
    
    val title = when(step) {
        1 -> stringResource(R.string.guide_step_1_title)
        2 -> stringResource(R.string.guide_step_2_title)
        3 -> stringResource(R.string.guide_step_3_title)
        else -> stringResource(R.string.guide_step_4_title)
    }
    
    val desc = when(step) {
        1 -> stringResource(R.string.guide_step_1_desc)
        2 -> stringResource(R.string.guide_step_2_desc)
        3 -> stringResource(R.string.guide_step_3_desc)
        else -> stringResource(R.string.guide_step_4_desc)
    }

    val icon = when(step) {
        1 -> Icons.Default.GppGood
        2 -> Icons.Default.Analytics
        3 -> Icons.AutoMirrored.Filled.VolumeUp
        else -> Icons.Default.Call
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.guide_title)) },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.weight(0.5f))
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = CircleShape,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(desc, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(totalSteps) { i ->
                    Box(
                        Modifier.size(width = if (step == i + 1) 24.dp else 8.dp, height = 8.dp)
                            .background(
                                if (step == i + 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                CircleShape
                            )
                    )
                }
            }

            Button(
                onClick = { if (step < totalSteps) step++ else onDone() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (step < totalSteps) stringResource(R.string.next) else stringResource(R.string.finish))
            }
        }
    }
}

@Composable 
private fun AlertHistory(viewModel: HistoryViewModel = hiltViewModel()) {
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

@Composable 
private fun Settings(onLanguage: () -> Unit, onAbout: () -> Unit, onGuide: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) { 
    val settings by viewModel.settings.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.settings)) }) }) { padding -> 
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { 
            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column {
                    ListItem(headlineContent = { Text(stringResource(R.string.voice_alerts)) }, supportingContent = { Text(stringResource(R.string.voice_alerts_desc)) }, leadingContent = { Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null) }, trailingContent = { Switch(settings.voiceAlertsEnabled, viewModel::setVoice) })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.how_it_works)) },
                        supportingContent = { Text(stringResource(R.string.view_guide)) },
                        leadingContent = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                        modifier = Modifier.clickable { onGuide() }
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    ListItem(headlineContent = { Text(stringResource(R.string.app_language)) }, supportingContent = { Text(when(settings.languageTag) { "hi" -> "हिन्दी"; "mr" -> "मराठी"; "bn" -> "বাংলা"; "gu" -> "ગુજરાતી"; "ta" -> "தமிழ்"; "te" -> "తెలుగు"; else -> "English" }) }, leadingContent = { Icon(Icons.Default.Language, contentDescription = null) }, modifier = Modifier.clickable { onLanguage() })
                }
            }
            Card(shape = MaterialTheme.shapes.large, modifier = Modifier.clickable { onAbout() }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) { ListItem(headlineContent = { Text(stringResource(R.string.about_gramkavach)) }, supportingContent = { Text(stringResource(R.string.version)) }, leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }) }
        } 
    } 
}

@Composable 
private fun LanguageSelection(onDone: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) { 
    val languages = listOf("English" to "en", "हिन्दी" to "hi", "বাংলা" to "bn", "मराठी" to "mr", "ગુજરાતી" to "gu", "தமிழ்" to "ta", "తెలుగు" to "te")
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.choose_language)) }) }) { padding -> LazyColumn(Modifier.padding(padding)) { items(languages) { (name, tag) -> ListItem(headlineContent = { Text(name) }, trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }, modifier = Modifier.clickable { viewModel.setLanguage(tag); onDone() }) } } } 
}

@Composable 
private fun About(onDone: () -> Unit) { 
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.about_gramkavach)) }, navigationIcon = { IconButton(onClick = onDone) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }) }) { padding -> 
        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) { 
            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(100.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.about_desc), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(stringResource(R.string.tech_stack), fontWeight = FontWeight.Bold); Text("• Kotlin & Jetpack Compose"); Text("• Hilt (Dependency Injection)"); Text("• Room (Local Database)"); Text("• ONNX Runtime Mobile"); Text("• Bhashini AI (Voice synthesis)") } }
            Spacer(Modifier.weight(1f))
            Text(stringResource(R.string.safer_india), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        } 
    } 
}
