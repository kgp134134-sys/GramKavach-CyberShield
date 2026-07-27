# Walkthrough - GramKavach Release Stability

I have addressed the build, Gradle, and runtime issues to ensure GramKavach is ready for hackathon judging. The app now generates a stable, signed Release APK and launches quickly into the Demo experience.

## Changes Made

### 🚀 Release Build & Optimization
- **Build Configuration**: Added a `release` build type in [app/build.gradle.kts](file:///C:/Project/gram%20kavacha/app/build.gradle.kts) with R8 minification and resource shrinking enabled.
- **Signed APK**: Configured the release build to use the debug signing key, ensuring it is ready for immediate installation and testing without complex keystore management.
- **Proguard/R8 Rules**: Created [app/proguard-rules.pro](file:///C:/Project/gram%20kavacha/app/proguard-rules.pro) to protect Hilt, Room, and Bhashini (Retrofit/Moshi) classes from obfuscation issues, preventing runtime crashes in the release build.

### 🛡️ Stability Improvements
- **Room Database**: Added `.fallbackToDestructiveMigration()` to the database builder in [DataModule.kt](file:///C:/Project/gram%20kavacha/data/src/main/kotlin/org/gramkavach/data/di/DataModule.kt) to ensure schema mismatches don't cause crashes on fresh judging devices.
- **Manifest Cleanup**: Consolidated the `FullScreenWarningActivity` flags in the main [AndroidManifest.xml](file:///C:/Project/gram%20kavacha/app/src/main/AndroidManifest.xml) and fixed a Lint error where the `RiskMonitoringService` was causing build failures during release assembly.

### ⚡ Demo Experience
- **Faster Launch**: Reduced the splash screen delay from 1.5 seconds to **0.8 seconds** in [MainActivity.kt](file:///C:/Project/gram%20kavacha/app/src/main/MainActivity.kt) to get judges into the demo UI faster.
- **Language Synchronization**: Standardized the default language tag to `en` across the UI and repository to ensure voice alerts work out-of-the-box.

## Verification Results

### Build Success
- Successfully generated the release APK using `./gradlew :app:assembleRelease`.
- APK Location: `app/build/outputs/apk/release/app-release.apk` (81.2 MB)

### Runtime Checks
- **Hilt**: Verified all ViewModels and Services are correctly annotated.
- **Permissions**: Verified the app handles missing permissions gracefully by fallback to safe monitoring states.
- **Bhashini**: Verified the voice alert speaker falls back to Android TTS if credentials are missing.

> [!TIP]
> **Hackathon Judges**: You can now share the `app-release.apk` directly. It is pre-signed and ready for "Demo Mode" testing using the buttons on the Home screen.
