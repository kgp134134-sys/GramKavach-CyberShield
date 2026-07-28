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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

    Scaffold(
        topBar = {
            HomeTopBar(userName = userName, onSettingsClick = { onOpen("settings") })
        },
        containerColor = CreamWarm
    ) { padding ->
        val latestScore = alerts.firstOrNull()?.score ?: 0
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            
            RiskScoreSection(
                score = latestScore,
                isLoading = isLoading,
                onCardClick = { if (!isLoading) showBreakdown = true }
            )

            Spacer(Modifier.height(24.dp))

            MonitoringStatusBadge()

            Spacer(Modifier.height(24.dp))

            Text(
                stringResource(R.string.how_it_works),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            ActionCardsSection(onOpen = onOpen)

            Spacer(Modifier.height(24.dp))

            Text(
                stringResource(R.string.quick_actions),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            QuickActionsGrid(
                alertCount = alerts.size,
                onHistoryClick = { onOpen("history") },
                onReportClick = {
                    context.startActivity(Intent(Intent.ACTION_DIAL, "tel:1930".toUri()))
                }
            )

            Spacer(Modifier.height(24.dp))

            SimulationControls(
                isLoading = isLoading,
                viewModel = viewModel
            )

            error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(userName: String, onSettingsClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_app_shield), 
                    contentDescription = null, 
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        stringResource(R.string.app_name),
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Text(
                        stringResource(R.string.hello_user, userName),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings), tint = Color.DarkGray)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWarm)
    )
}

@Composable
private fun RiskScoreSection(
    score: Int,
    isLoading: Boolean,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading) { onCardClick() },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RiskGauge(score = score, isScanning = isLoading)
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = when {
                    score >= 60 -> stringResource(R.string.high_risk_badge)
                    score > 0 -> "Risk Detected ⚠️"
                    else -> "System Protected ✅"
                },
                style = MaterialTheme.typography.titleLarge,
                color = when {
                    score >= 60 -> HighRed
                    score > 0 -> ModerateOrange
                    else -> SafeGreen
                },
                fontWeight = FontWeight.Bold
            )
            
            Text(
                stringResource(R.string.tap_hint) + " \u24D8",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun MonitoringStatusBadge() {
    Surface(
        color = Color(0xFFE8F5E9),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SafeGreen,
                modifier = Modifier.size(18.dp)
            )
            Text(
                "Real-time monitoring active",
                style = MaterialTheme.typography.labelLarge,
                color = SafeGreen,
                fontWeight = FontWeight.Bold
            )
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F1E9)), // Matches image light green
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF98C1A3).copy(alpha = 0.5f))
    ) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Safety Rules (Must Read)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    "Padhiye aur surakshit rahiye",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF1B5E20))
        }
    }

    Spacer(Modifier.height(16.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen("guide") },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1EBF5)), // Matches image light violet
    ) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFA04000), modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "View User Guide",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "GramKavach is a local-first digital financial",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
        }
    }
}

@Composable
private fun QuickActionsGrid(
    alertCount: Int,
    onHistoryClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.weight(1f).clickable { onHistoryClick() },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
        ) {
            Column(Modifier.padding(16.dp)) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(12.dp))
                Text("History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$alertCount alerts", style = MaterialTheme.typography.bodySmall)
            }
        }
        Card(
            modifier = Modifier.weight(1f).clickable { onReportClick() },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(Modifier.padding(16.dp)) {
                Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(12.dp))
                Text("1930 Help", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                Text("Report Fraud", style = MaterialTheme.typography.bodySmall, color = Color.Black)
            }
        }
    }
}

@Composable
private fun SimulationControls(isLoading: Boolean, viewModel: HomeViewModel) {
    Text(
        "Hackathon Demo (4 Levels)",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(12.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SimulationButton("1. Simulate Safe (Score 0)", SafeGreen, isLoading, viewModel::simulateSafe)
            SimulationButton("2. Simulate Phishing (Score 25)", CautionAmber, isLoading, viewModel::simulatePhishing)
            SimulationButton("3. Simulate UPI Collect (Score 50)", ModerateOrange, isLoading, viewModel::simulateCollectRequest)
            SimulationButton("4. Simulate Bank Scam (Score 85)", HighRed, isLoading, viewModel::simulateBankScam)
        }
    }
}

@Composable
private fun SimulationButton(text: String, color: Color, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        enabled = !isLoading,
        shape = CircleShape
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RiskGauge(score: Int, isScanning: Boolean, modifier: Modifier = Modifier) {
    val animatedScore by animateIntAsState(targetValue = score, label = "ScoreValue")
    val infiniteTransition = rememberInfiniteTransition(label = "Scanning")
    val scanRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearEasing)),
        label = "ScanRotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            // Background track
            drawArc(
                color = Color(0xFFF5F5F5),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )
            if (isScanning) {
                drawArc(
                    color = SaffronDeep,
                    startAngle = scanRotation,
                    sweepAngle = 60f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            } else {
                drawArc(
                    color = UiUtils.getRiskColor(score),
                    startAngle = 150f,
                    sweepAngle = (animatedScore / 100f) * 240f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        
        // Gauge Labels
        Box(Modifier.size(200.dp)) {
            Text("0", Modifier.align(Alignment.BottomStart).padding(start = 32.dp, bottom = 32.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("100", Modifier.align(Alignment.BottomEnd).padding(end = 32.dp, bottom = 32.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isScanning) "--" else animatedScore.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = if (isScanning) Color.Gray else UiUtils.getRiskColor(score)
            )
            Text(
                text = "Risk Score",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
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
