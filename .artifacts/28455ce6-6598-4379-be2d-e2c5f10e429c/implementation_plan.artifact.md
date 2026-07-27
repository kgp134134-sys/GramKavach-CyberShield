# Implementation Plan - Modernizing MainActivity and Refactoring

The current implementation of `MainActivity` in `org.gramkavach.app` is a large monolithic file that handles everything from Activity lifecycle and permissions to the entire UI logic of the application. The `onCreate` method itself is functional but lacks modern Android best practices like edge-to-edge support and the standard Splash Screen API.

## User Review Required

> [!IMPORTANT]
> I am proposing a significant refactoring to extract screen composables into their own files. This will greatly improve readability and maintainability but will change the file structure of the `:app` module.

> [!NOTE]
> I will add the `androidx.core:core-splashscreen` dependency to handle the app startup experience more professionally.

## Proposed Changes

### 1. Build Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Project/gram kavacha/app/build.gradle.kts)
- Add `androidx.core:core-splashscreen` dependency.

### 2. MainActivity Modernization

#### [MODIFY] [MainActivity.kt](file:///C:/Project/gram kavacha/app/src/main/kotlin/org/gramkavach/app/MainActivity.kt)
- Add `installSplashScreen()` to handle the cold start.
- Add `enableEdgeToEdge()` to support modern immersive displays.
- Extract the 800+ lines of Composables into a new package `org.gramkavach.app.ui.screens`.

### 3. UI Refactoring (New Package: `org.gramkavach.app.ui.screens`)

#### [NEW] `HomeScreen.kt`
- Extract `Home`, `RiskGauge`, `RiskBreakdownSheet`, `DetectionAnalysisSection` composables.

#### [NEW] `AuthScreens.kt`
- Extract `AuthLanding`, `Onboarding` composables.

#### [NEW] `RiskAlertScreen.kt`
- Extract `RiskAlert` composable.

#### [NEW] `InfoScreens.kt`
- Extract `UserGuide`, `About`, `SafetyManual` composables.

#### [NEW] `HistoryScreen.kt`
- Extract `AlertHistory` composable.

#### [NEW] `ProfileScreen.kt`
- Extract `ProfileScreen` composable.

#### [NEW] `SettingsScreen.kt`
- Extract `Settings`, `LanguageSelection` composables.

#### [NEW] `CommonComponents.kt`
- Extract `RangoliPattern`, `Splash` (if still needed for internal transition) composables.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure everything compiles after refactoring.
- Run existing unit tests: `./gradlew :app:test`.

### Manual Verification
- Deploy the app and verify that the splash screen transitions smoothly.
- Check that the UI correctly respects system bars (Edge-to-Edge).
- Verify that navigation between screens still works as expected.
