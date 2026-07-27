package org.gramkavach.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gramkavach.app.R
import org.gramkavach.app.ui.theme.*

@Composable
fun AuthLanding(onCreate: () -> Unit) {
    Box(Modifier.fillMaxSize().background(CreamWarm), contentAlignment = Alignment.Center) {
        RangoliPattern(Modifier.size(500.dp).align(Alignment.TopCenter).offset(y = (-100).dp))
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(100.dp))
            Surface(color = Color.White, shape = CircleShape, shadowElevation = 4.dp) {
                Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(120.dp).padding(16.dp))
            }
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = SaffronDeep)
            Text(stringResource(R.string.tagline), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            
            Spacer(Modifier.height(32.dp))
            
            Button(
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronDeep)
            ) {
                Text(stringResource(R.string.create_account_btn), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.slogan_india), style = MaterialTheme.typography.titleMedium, color = EarthTerracotta, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Onboarding(title: String, onComplete: (String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(CreamWarm).padding(24.dp)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SaffronDeep, textAlign = TextAlign.Center)
            Text(stringResource(R.string.onboarding_desc), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name_label), fontSize = 16.sp) },
                placeholder = { Text(stringResource(R.string.name_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium),
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronDeep, focusedLabelColor = SaffronDeep)
            )
        }

        val isReady = name.isNotBlank()
        Button(
            onClick = { if (isReady) onComplete(name) },
            enabled = isReady,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(60.dp).padding(bottom = 8.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = SaffronDeep,
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            )
        ) {
            Text(stringResource(R.string.start_journey), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
