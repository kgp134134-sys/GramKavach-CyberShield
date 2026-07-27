# Implementation Plan - Architecture Optimization & Project Cleanup

The goal is to modernize the app's entry point (`MainActivity`), refactor the monolithic UI into a clean modular structure, and clean up redundant files in the project. We will also update the project documentation (`README.md`) while preserving its original design.

## User Review Required

> [!IMPORTANT]
> **Project Cleanup**: I will delete the `GRAMKAVACHA/` directory as it appears to be a redundant copy of the project.
> **Architecture**: I will move all UI screens from `MainActivity.kt` to a new package `org.gramkavach.app.ui.screens`. This is a major structural change for the `:app` module.

> [!NOTE]
> I will add the `androidx.core:core-splashscreen` dependency and use `enableEdgeToEdge()` as discussed to improve the modern feel of the app.

## Proposed Changes

### 1. Build & Dependency Updates
#### [MODIFY] [build.gradle.kts](file:///C:/Project/gram%20kavacha/app/build.gradle.kts)
- Add `androidx.core:core-splashscreen:1.0.1` dependency.

### 2. MainActivity Refactoring
#### [MODIFY] [MainActivity.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/MainActivity.kt)
- Implement `installSplashScreen()` and `enableEdgeToEdge()`.
- Extract all `@Composable` functions into separate files in `org.gramkavach.app.ui.screens`.

### 3. New UI Architecture (Package: `org.gramkavach.app.ui.screens`)
- **[NEW] `AuthScreens.kt`**: `AuthLanding`, `Onboarding`.
- **[NEW] `HomeScreen.kt`**: `Home`, `RiskGauge`, `RiskBreakdownSheet`.
- **[NEW] `RiskAlertScreen.kt`**: `RiskAlert`, `DetectionAnalysisSection`.
- **[NEW] `InfoScreens.kt`**: `UserGuide`, `About`, `SafetyManual`.
- **[NEW] `HistoryScreen.kt`**: `AlertHistory`.
- **[NEW] `ProfileScreen.kt`**: `ProfileScreen`.
- **[NEW] `SettingsScreen.kt`**: `Settings`, `LanguageSelection`.
- **[NEW] `CommonComponents.kt`**: `RangoliPattern`, `Splash`.

### 4. Project Cleanup
- **[DELETE] `GRAMKAVACHA/`**: Remove the entire duplicate directory.

### 5. Documentation Update
#### [MODIFY] [README.md](file:///C:/Project/gram%20kavacha/README.md)
- Update the **Architecture** section to reflect the new UI modularization.
- Ensure all screenshot links are correct and aligned with the latest UI.

## Verification Plan

### Automated Tests
- Build the project: `./gradlew :app:assembleDebug`.
- Run lint to check for any issues with the new structure.

### Manual Verification
- Deploy to a device/emulator.
- Verify the Splash Screen animation.
- Navigate through all screens to ensure DI (Hilt) and ViewModels are still working correctly.
