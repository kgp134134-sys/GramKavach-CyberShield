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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
import org.gramkavach.app.ui.utils.UiUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    userName: String,
    onAssess: (RiskAssessment) -> Unit,
    onOpen: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
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

    LaunchedEffect(assessment) {
        assessment?.let {
            onAssess(it)
            viewModel.consumeAssessment()
        }
    }

    if (showBreakdown) {
        val latestAlert = alerts.firstOrNull()
        ModalBottomSheet(onDismissRequest = { showBreakdown = false }, sheetState = sheetState) {
            RiskBreakdownSheet(latestAlert)
            Spacer(Modifier.height(32.dp))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RangoliBackground()
        
        Scaffold(
            topBar = {
                HomeTopBar(userName = userName, onSettingsClick = { onOpen("settings") })
            },
            containerColor = Color.Transparent
        ) { padding ->
            val latestScore = alerts.firstOrNull()?.score ?: 0
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RiskScoreSection(
                    score = latestScore,
                    isLoading = isLoading,
                    hasOverlayPermission = hasOverlayPermission,
                    onCardClick = { if (!isLoading) showBreakdown = true }
                )

                MonitoringStatusBadge(
                    score = latestScore,
                    hasOverlayPermission = hasOverlayPermission
                )

                Text(
                    stringResource(R.string.how_it_works),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    color = EarthTerracotta
                )

                ActionCardsSection(onOpen = onOpen)

                Text(
                    stringResource(R.string.quick_actions),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    color = EarthTerracotta
                )

                QuickActionsGrid(
                    alertCount = alerts.size,
                    onHistoryClick = { onOpen("history") },
                    onReportClick = {
                        context.startActivity(Intent(Intent.ACTION_DIAL, "tel:1930".toUri()))
                    }
                )

                PermissionWarning(
                    hasOverlayPermission = hasOverlayPermission,
                    onEnableClick = {
                        context.startActivity(
                            Intent(
                                AndroidSettings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                "package:${context.packageName}".toUri()
                            )
                        )
                    }
                )

                SimulationControls(
                    isLoading = isLoading,
                    viewModel = viewModel
                )

                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RangoliBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "RangoliRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing)
        ),
        label = "Rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerPoint = size.center
        rotate(rotation, pivot = centerPoint) {
            val patternColor = SaffronDeep.copy(alpha = 0.03f)
            val strokeWidth = 1.dp.toPx()
            
            for (i in 0 until 8) {
                rotate(i * 45f) {
                    drawArc(
                        color = patternColor,
                        startAngle = -20f,
                        sweepAngle = 40f,
                        useCenter = true,
                        topLeft = Offset(centerPoint.x - 300.dp.toPx(), centerPoint.y - 300.dp.toPx()),
                        size = Size(600.dp.toPx(), 600.dp.toPx()),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(userName: String, onSettingsClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "🛡️ " + stringResource(R.string.app_name),
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge,
                    color = SaffronDeep
                )
                Text(
                    stringResource(R.string.hello_user, userName),
                    style = MaterialTheme.typography.labelSmall,
                    color = EarthTerracotta.copy(alpha = 0.7f)
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings), tint = EarthTerracotta)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun RiskScoreSection(
    score: Int,
    isLoading: Boolean,
    hasOverlayPermission: Boolean,
    onCardClick: () -> Unit
) {
    val currentStatusColor = UiUtils.getRiskColor(score)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading) { onCardClick() },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RiskGauge(score = score, isScanning = isLoading)
            Spacer(Modifier.height(16.dp))
            val status = when {
                isLoading -> stringResource(R.string.analyzing_context)
                score >= 60 -> stringResource(R.string.risk_detected) + ": " + stringResource(R.string.high_risk_badge)
                score > 0 -> stringResource(R.string.risk_detected) + " ⚠️"
                hasOverlayPermission -> stringResource(R.string.system_protected) + " ✅"
                else -> stringResource(R.string.setup_required) + " ⚠️"
            }
            Text(
                text = status,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = if (isLoading) SaffronDeep else if (score > 0) currentStatusColor else if (hasOverlayPermission) SafeGreen else HighRed,
                fontWeight = FontWeight.Bold
            )
            if (!isLoading) {
                Text(
                    stringResource(R.string.tap_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun MonitoringStatusBadge(score: Int, hasOverlayPermission: Boolean) {
    AnimatedVisibility(visible = hasOverlayPermission) {
        val badgeColor = if (score > 0) UiUtils.getRiskColor(score) else SafeGreen
        Surface(
            color = badgeColor.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.medium,
            border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    if (score > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    if (score > 0) stringResource(R.string.risk_monitoring_active) else stringResource(R.string.monitoring_active),
                    style = MaterialTheme.typography.labelMedium,
                    color = badgeColor
                )
            }
        }
    }
}

@Composable
private fun ActionCardsSection(onOpen: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen("manual") },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = LeafGreen.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, LeafGreen.copy(alpha = 0.2f))
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = LeafGreen)
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.safety_manual_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )
                Text(
                    stringResource(R.string.safety_manual_subtitle),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = LeafGreen
            )
        }
    }

    Spacer(Modifier.height(8.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen("guide") },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = SaffronDeep.copy(alpha = 0.05f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, SaffronDeep.copy(alpha = 0.1f))
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = SaffronDeep)
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.view_guide),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SaffronDeep
                )
                Text(
                    stringResource(R.string.about_desc),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = SaffronDeep
            )
        }
    }
}

@Composable
private fun QuickActionsGrid(
    alertCount: Int,
    onHistoryClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onHistoryClick() },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = CreamWarm.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, EarthTerracotta.copy(alpha = 0.1f))
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.History, contentDescription = null, tint = EarthTerracotta)
                Text(stringResource(R.string.history), style = MaterialTheme.typography.titleSmall, color = EarthTerracotta)
                Text(
                    stringResource(R.string.alerts_count, alertCount),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onReportClick() },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f))
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Text(
                    stringResource(R.string.help_1930),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Text(stringResource(R.string.report_fraud), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PermissionWarning(hasOverlayPermission: Boolean, onEnableClick: () -> Unit) {
    AnimatedVisibility(visible = !hasOverlayPermission) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = HighRed.copy(alpha = 0.05f)),
            shape = MaterialTheme.shapes.large,
            border = androidx.compose.foundation.BorderStroke(1.dp, HighRed.copy(alpha = 0.2f))
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = HighRed)
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.overlay_disabled),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = HighRed
                    )
                    Text(
                        stringResource(R.string.overlay_required),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Button(
                    onClick = onEnableClick,
                    colors = ButtonDefaults.buttonColors(containerColor = HighRed)
                ) {
                    Text(stringResource(R.string.enable))
                }
            }
        }
    }
}

@Composable
private fun SimulationControls(isLoading: Boolean, viewModel: HomeViewModel) {
    Text(
        stringResource(R.string.sim_controls_title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
        fontWeight = FontWeight.Bold,
        color = EarthTerracotta
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            EarthTerracotta.copy(alpha = 0.1f)
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = viewModel::simulateSafe,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.sim_safe))
            }
            Button(
                onClick = viewModel::simulatePhishing,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CautionAmber),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.sim_phishing), color = Color.Black)
            }
            Button(
                onClick = viewModel::simulateCollectRequest,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ModerateOrange),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.sim_collect))
            }
            Button(
                onClick = viewModel::simulateBankScam,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = HighRed),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.sim_bank_scam))
            }
        }
    }
}

@Composable
fun RiskGauge(score: Int, isScanning: Boolean, modifier: Modifier = Modifier) {
    val color by animateColorAsState(
        targetValue = UiUtils.getRiskColor(score),
        label = "GaugeColor"
    )
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
            drawArc(
                color = Color.LightGray.copy(alpha = 0.2f),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )
            if (isScanning) {
                drawArc(
                    color = SaffronDeep,
                    startAngle = scanRotation,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            } else {
                drawArc(
                    color = color,
                    startAngle = 140f,
                    sweepAngle = (animatedScore / 100f) * 260f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        Box(Modifier.size(200.dp)) {
            Text(
                stringResource(R.string.gauge_min),
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 32.dp),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                stringResource(R.string.gauge_max),
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 32.dp, bottom = 32.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isScanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = SaffronDeep,
                    strokeWidth = 3.dp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.analyzing_text),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SaffronDeep
                )
            } else {
                Text(
                    text = animatedScore.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Text(
                    text = stringResource(R.string.risk_score_label),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun RiskBreakdownSheet(latestAlert: AlertRecord?) {
    val factors = listOf(
        Pair(
            stringResource(R.string.factor_sms_link),
            (latestAlert?.reasons?.any { it.contains("SMS", true) || it.contains("link", true) } ?: false)
        ),
        Pair(
            stringResource(R.string.factor_remote_apps),
            (latestAlert?.reasons?.any { it.contains("remote", true) || it.contains("screen", true) } ?: false)
        ),
        Pair(
            stringResource(R.string.factor_upi_requests),
            (latestAlert?.reasons?.any { it.contains("collect", true) } ?: false)
        ),
        Pair(
            stringResource(R.string.factor_permissions),
            (latestAlert?.reasons?.any { it.contains("accessibility", true) } ?: false)
        )
    )

    Column(
        Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            stringResource(R.string.breakdown_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = SaffronDeep
        )
        HorizontalDivider(color = SaffronDeep.copy(alpha = 0.1f))

        factors.forEach { (name, isRisk) ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Surface(
                    color = if (isRisk) HighRed.copy(alpha = 0.1f) else SafeGreen.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isRisk) HighRed.copy(alpha = 0.5f) else SafeGreen.copy(alpha = 0.5f)
                    )
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = CardDefaults.cardColors(containerColor = SaffronDeep.copy(alpha = 0.05f))
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Shield, null, tint = SaffronDeep)
                Text(
                    text = if ((latestAlert?.score ?: 0) == 0) {
                        stringResource(R.string.device_protected_desc)
                    } else {
                        stringResource(R.string.risks_detected_desc)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
