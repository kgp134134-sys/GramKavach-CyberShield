package org.gramkavach.app.ui.screens

import android.content.Intent
import android.provider.Settings as AndroidSettings
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.gramkavach.app.R
import org.gramkavach.app.ui.theme.*
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.domain.model.AlertRecord
import org.gramkavach.app.HomeViewModel
import org.gramkavach.app.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(userName: String, onAssess: (RiskAssessment) -> Unit, onOpen: (String) -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val assessment by viewModel.assessment.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val alerts by hiltViewModel<HistoryViewModel>().alerts.collectAsState()
    var hasOverlayPermission by remember { mutableStateOf(AndroidSettings.canDrawOverlays(context)) }
    
    var showBreakdown by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasOverlayPermission = AndroidSettings.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(assessment) { assessment?.let { onAssess(it); viewModel.consumeAssessment() } }

    if (showBreakdown) {
        val latestAlert = if (alerts.isNotEmpty()) alerts.first() else null
        ModalBottomSheet(onDismissRequest = { showBreakdown = false }, sheetState = sheetState) {
            RiskBreakdownSheet(latestAlert)
            Spacer(Modifier.height(32.dp))
        }
    }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("🛡️ " + stringResource(R.string.app_name), fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = SaffronDeep); Text(stringResource(R.string.hello_user, userName), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline) } }, actions = { IconButton(onClick = { onOpen("settings") }) { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) } }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val latestScore = if (alerts.isNotEmpty()) alerts.first().score else 0
                val currentStatusColor = when { latestScore >= 60 -> HighRed; latestScore >= 40 -> ModerateOrange; latestScore >= 15 -> CautionAmber; else -> SafeGreen }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable(enabled = !isLoading) { showBreakdown = true },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        RiskGauge(score = latestScore, isScanning = isLoading)
                        Spacer(Modifier.height(16.dp))
                        val status = when { 
                            isLoading -> "Analyzing context..."
                            latestScore >= 60 -> stringResource(R.string.risk_detected) + ": " + stringResource(R.string.high_risk_badge)
                            latestScore > 0 -> stringResource(R.string.risk_detected) + " ⚠️"
                            hasOverlayPermission -> stringResource(R.string.system_protected) + " ✅"
                            else -> stringResource(R.string.setup_required) + " ⚠️" 
                        }
                        Text(
                            text = status, 
                            style = MaterialTheme.typography.titleLarge, 
                            textAlign = TextAlign.Center,
                            color = if (isLoading) SaffronDeep else if (latestScore > 0) currentStatusColor else if (hasOverlayPermission) SafeGreen else HighRed, 
                            fontWeight = FontWeight.Bold
                        )
                        if (!isLoading) {
                            Text(stringResource(R.string.tap_hint), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 8.dp))
                        }
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
                    modifier = Modifier.fillMaxWidth().clickable { onOpen("manual") },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = LeafGreen.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LeafGreen.copy(alpha = 0.3f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = LeafGreen)
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.safety_manual_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = LeafGreen)
                            Text("Padiye aur surakshit rahiye", style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp), tint = LeafGreen)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onOpen("guide") },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
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
                Text("Hackathon Simulation Controls", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
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
fun RiskGauge(score: Int, isScanning: Boolean, modifier: Modifier = Modifier) {
    val color by animateColorAsState(targetValue = when { score >= 60 -> HighRed; score >= 40 -> ModerateOrange; score >= 15 -> CautionAmber; else -> SafeGreen }, label = "GaugeColor")
    val animatedScore by animateIntAsState(targetValue = score, label = "ScoreValue")
    
    val infiniteTransition = rememberInfiniteTransition(label = "Scanning")
    val scanRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearEasing)),
        label = "ScanRotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawArc(color = Color.LightGray.copy(alpha = 0.2f), startAngle = 140f, sweepAngle = 260f, useCenter = false, style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round))
            if (isScanning) {
                drawArc(
                    color = SaffronDeep, 
                    startAngle = scanRotation, 
                    sweepAngle = 90f, 
                    useCenter = false, 
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            } else {
                drawArc(color = color, startAngle = 140f, sweepAngle = (animatedScore / 100f) * 260f, useCenter = false, style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round))
            }
        }
        Box(Modifier.size(200.dp)) {
            Text("0", Modifier.align(Alignment.BottomStart).padding(start = 32.dp, bottom = 32.dp), style = MaterialTheme.typography.labelSmall)
            Text("100", Modifier.align(Alignment.BottomEnd).padding(end = 32.dp, bottom = 32.dp), style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isScanning) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp), color = SaffronDeep, strokeWidth = 3.dp)
                Spacer(Modifier.height(8.dp))
                Text(text = stringResource(R.string.analyzing_text), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SaffronDeep)
            } else {
                Text(text = animatedScore.toString(), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = color)
                Text(text = stringResource(R.string.risk_score_label), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun RiskBreakdownSheet(latestAlert: AlertRecord?) {
    val factors = listOf(
        Pair(stringResource(R.string.factor_sms_link), (latestAlert?.reasons?.any { it.contains("SMS", true) || it.contains("link", true) } ?: false)),
        Pair(stringResource(R.string.factor_remote_apps), (latestAlert?.reasons?.any { it.contains("remote", true) || it.contains("screen", true) } ?: false)),
        Pair(stringResource(R.string.factor_upi_requests), (latestAlert?.reasons?.any { it.contains("collect", true) } ?: false)),
        Pair(stringResource(R.string.factor_permissions), (latestAlert?.reasons?.any { it.contains("accessibility", true) } ?: false))
    )

    Column(Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.breakdown_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SaffronDeep)
        HorizontalDivider(color = SaffronDeep.copy(alpha = 0.1f))
        
        factors.forEach { (name, isRisk) ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Surface(
                    color = if (isRisk) HighRed.copy(alpha = 0.1f) else SafeGreen.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isRisk) HighRed.copy(alpha = 0.5f) else SafeGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        if (isRisk) stringResource(R.string.risk_detected_status) else stringResource(R.string.safe_status),
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isRisk) HighRed else SafeGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = CardDefaults.cardColors(containerColor = SaffronDeep.copy(alpha = 0.05f))
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Shield, null, tint = SaffronDeep)
                Text(
                    text = if ((latestAlert?.score ?: 0) == 0) "Your device is fully protected." else "Some risks detected. Stay vigilant.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
