# Implementation Plan - Stable Release & Demo Optimization

This plan addresses build, Gradle, and runtime stability for GramKavach, ensuring the app is ready for hackathon judging and direct-to-demo installation.

## User Review Required

> [!IMPORTANT]
> **Release Signing**: For the hackathon build, I will configure the `release` build type to use the `debug` signing key. This ensures the APK is signed and installable on any device without requiring the judge to manage keystores, while still benefiting from R8 optimizations.

## Proposed Changes

### Build & Dependencies
#### [MODIFY] [app/build.gradle.kts](file:///C:/Project/gram%20kavacha/app/build.gradle.kts)
- Add `buildTypes` block with `release` configuration.
- Enable `minifyEnabled` and `shrinkResources` for the release build.
- Configure `signingConfig` to use `signingConfigs.debug` for the release build.

#### [NEW] [app/proguard-rules.pro](file:///C:/Project/gram%20kavacha/app/proguard-rules.pro)
- Add essential R8/Proguard rules for:
    - **Hilt**: Keep `@HiltAndroidApp` and associated classes.
    - **Room**: Keep entity classes and DAO implementations.
    - **Retrofit/Moshi**: Keep data classes used for Bhashini API.
    - **ONNX Runtime**: Ensure native libraries are not stripped or misaligned.

### Stability & Logic
#### [MODIFY] [data/di/DataModule.kt](file:///C:/Project/gram%20kavacha/data/src/main/kotlin/org/gramkavach/data/di/DataModule.kt)
- Add `.fallbackToDestructiveMigration()` to the Room database builder. This prevents crashes if the local database schema becomes out of sync during the judging process.

#### [MODIFY] [app/src/main/AndroidManifest.xml](file:///C:/Project/gram%20kavacha/app/src/main/AndroidManifest.xml)
- Consolidate `FullScreenWarningActivity` flags to ensure it correctly shows over the lock screen and turns the screen on when an alert is triggered.

### Demo Optimization
#### [MODIFY] [app/src/main/kotlin/org/gramkavach/app/MainActivity.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/MainActivity.kt)
- Reduce splash screen delay from 1.5s to 0.8s for a faster "Launch to Demo" experience.
- Ensure consistent language tag defaults between `RiskRepositoryImpl` and the UI.

#### [MODIFY] [data/src/main/kotlin/org/gramkavach/data/RiskRepositoryImpl.kt](file:///C:/Project/gram%20kavacha/data/src/main/kotlin/org/gramkavach/data/RiskRepositoryImpl.kt)
- Change default language tag from `en-IN` to `en` to align with the UI's language selection tags.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleRelease` to verify the build process completes with R8 enabled.

### Manual Verification
- I will perform a final review of the generated `AndroidManifest.xml` and `build.gradle.kts` to ensure no syntax errors.
- Confirm that `ResilientVoiceAlertSpeaker` and `HybridRiskEngine` remain stable even with empty credentials.
