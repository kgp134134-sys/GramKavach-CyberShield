package org.gramkavach.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.gramkavach.app.ui.screens.*
import org.gramkavach.app.ui.theme.GramKavachTheme
import org.gramkavach.bhashini.BhashiniTextToSpeech
import org.gramkavach.bhashini.ResilientVoiceAlertSpeaker
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.monitoring.RiskMonitoringService

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { startMonitoring() }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        requestMonitoringPermissions()
        
        setContent {
            GramKavachApp()
        }
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
        val settingsViewModel = hiltViewModel<SettingsViewModel>()
        val userSettings by settingsViewModel.settings.collectAsState()
        val scope = rememberCoroutineScope()

        // Sync system language on first start
        LaunchedEffect(Unit) {
            if (userSettings.userName.isEmpty()) {
                val systemLanguage = java.util.Locale.getDefault().language
                val supportedLanguages = listOf("hi", "mr", "bn", "gu", "ta", "te")
                if (systemLanguage in supportedLanguages && systemLanguage != userSettings.languageTag) {
                    settingsViewModel.setLanguage(systemLanguage)
                }
            }
        }

        LaunchedEffect(userSettings.languageTag) {
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(userSettings.languageTag)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        NavHost(nav, startDestination = "splash") {
            composable("splash") { 
                Splash { 
                    if (userSettings.userName.isEmpty()) {
                        nav.navigate("landing") { popUpTo("splash") { inclusive = true } }
                    } else {
                        nav.navigate("home") { popUpTo("splash") { inclusive = true } }
                    }
                } 
            }
            composable("landing") { 
                AuthLanding(
                    onCreate = { nav.navigate("register") }
                )
            }
            composable("login") {
                Onboarding(
                    title = stringResource(R.string.login_btn),
                    onComplete = { name ->
                        scope.launch {
                            settingsViewModel.updateUserSettings(userSettings.copy(userName = name, languageTag = "en"))
                            nav.navigate("home") { popUpTo("landing") { inclusive = true } }
                        }
                    }
                )
            }
            composable("register") {
                Onboarding(
                    title = stringResource(R.string.create_account_btn),
                    onComplete = { name ->
                        scope.launch {
                            settingsViewModel.updateUserSettings(userSettings.copy(userName = name, languageTag = "en"))
                            nav.navigate("home") { popUpTo("landing") { inclusive = true } }
                        }
                    }
                )
            }
            composable("home") { 
                Home(
                    userName = userSettings.userName, 
                    onAssess = { assessment = it; nav.navigate("risk") }, 
                    onOpen = nav::navigate 
                ) 
            }
            composable("risk") { RiskAlert(assessment, speaker, onDismiss = { nav.popBackStack() }) }
            composable("history") { AlertHistory() }
            composable("guide") { UserGuide { nav.popBackStack() } }
            composable("manual") { SafetyManual { nav.popBackStack() } }
            composable("profile") { 
                ProfileScreen(
                    userName = userSettings.userName,
                    onDelete = { 
                        scope.launch {
                            settingsViewModel.resetUser()
                            nav.navigate("landing") { popUpTo(0) { inclusive = true } }
                        }
                    },
                    onBack = { nav.popBackStack() }
                ) 
            }
            composable("settings") { 
                Settings(
                    onLanguage = { nav.navigate("language") },
                    onAbout = { nav.navigate("about") }, 
                    onGuide = { nav.navigate("guide") },
                    onProfile = { nav.navigate("profile") }
                ) 
            }
            composable("language") { LanguageSelection(onDone = { nav.popBackStack() }) }
            composable("about") { About { nav.popBackStack() } }
        }
    }
}
