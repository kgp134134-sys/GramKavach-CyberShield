package org.gramkavach.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.gramkavach.app.R
import org.gramkavach.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuide(onDone: () -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable 
fun About(onDone: () -> Unit) { 
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyManual(onBack: () -> Unit) {
    val donts = listOf(
        stringResource(R.string.tip_dont_1),
        stringResource(R.string.tip_dont_2),
        stringResource(R.string.tip_dont_3),
        stringResource(R.string.tip_dont_4),
        stringResource(R.string.tip_dont_5),
        stringResource(R.string.tip_dont_6)
    )
    val dos = listOf(
        stringResource(R.string.tip_do_1),
        stringResource(R.string.tip_do_2),
        stringResource(R.string.tip_do_3),
        stringResource(R.string.tip_do_4)
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.safety_manual_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // Card 1: Don'ts (Avoid)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HighRed.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.Block, null, tint = HighRed, modifier = Modifier.size(28.dp))
                            Text(stringResource(R.string.manual_donts_header), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = HighRed)
                        }
                        HorizontalDivider(color = HighRed.copy(alpha = 0.1f))
                        donts.forEach { point ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("•", style = MaterialTheme.typography.headlineSmall, color = HighRed, fontWeight = FontWeight.Bold)
                                Text(point, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Card 2: Do's (Remember)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SafeGreen.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.CheckCircle, null, tint = SafeGreen, modifier = Modifier.size(28.dp))
                            Text(stringResource(R.string.manual_dos_header), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = SafeGreen)
                        }
                        HorizontalDivider(color = SafeGreen.copy(alpha = 0.1f))
                        dos.forEach { point ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Done, null, tint = SafeGreen, modifier = Modifier.size(20.dp).padding(top = 4.dp))
                                Text(point, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = SafeGreen.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SaffronDeep)) {
                    Text(stringResource(R.string.slogan_india), Modifier.padding(24.dp).fillMaxWidth(), textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
