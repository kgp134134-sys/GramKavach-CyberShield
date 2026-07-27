package org.gramkavach.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.gramkavach.app.R
import org.gramkavach.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userName: String, onDelete: () -> Unit, onBack: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_desc)) },
            confirmButton = { Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = HighRed)) { Text(stringResource(R.string.delete_account_btn)) } },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.profile_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Surface(color = SaffronDeep.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(120.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, modifier = Modifier.size(64.dp), tint = SaffronDeep) }
            }
            Text(userName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.joined_on, SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())), style = MaterialTheme.typography.bodyMedium)
            
            Spacer(Modifier.weight(1f))
            
            Button(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EarthTerracotta),
                shape = MaterialTheme.shapes.large
            ) {
                Text(stringResource(R.string.delete_account_btn))
            }
        }
    }
}
