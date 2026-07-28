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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamWarm)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = SaffronDeep,
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )

            Text(
                stringResource(R.string.onboarding_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(
                        stringResource(R.string.name_label),
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronDeep,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = SaffronDeep
                )
            )
        }

        Spacer(Modifier.weight(1f))

        val isReady = name.isNotBlank()
        Button(
            onClick = { if (isReady) onComplete(name) },
            enabled = isReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = SaffronDeep,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color.Gray
            )
        ) {
            Text(
                stringResource(R.string.start_journey),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Spacer(Modifier.height(32.dp)) // Extra safety space at the bottom
    }
}
