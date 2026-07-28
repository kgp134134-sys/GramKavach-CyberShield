# Implementation Plan - Complete Language Transition Fix

This plan ensures that every piece of text on the Home (Meter Gauge) page is correctly pulled from string resources, allowing for a complete and seamless transition to the user's default system language.

## User Review Required

> [!IMPORTANT]
> - I will be replacing all hardcoded English strings in `HomeScreen.kt` and `HomeViewModel.kt` with dynamic resource lookups.
> - I will also ensure that the "translatable" flags in `strings.xml` are correctly set so that the Android system doesn't block translations.

## Proposed Changes

### 📱 Presentation Layer (:app)

#### [MODIFY] [HomeScreen.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/ui/screens/HomeScreen.kt)
- **Status Text**: Replace hardcoded "Risk Detected ⚠️" and "System Protected ✅" with `stringResource(R.string.risk_detected)` and `stringResource(R.string.system_protected)`.
- **Monitoring Badge**: Replace hardcoded "Real-time monitoring active" with `stringResource(R.string.monitoring_active)`.
- **Action Cards**:
    - "Safety Rules (Must Read)" -> `stringResource(R.string.safety_manual_title)`
    - "Padiye aur surakshit rahiye" -> `stringResource(R.string.safety_manual_subtitle)`
    - "View User Guide" -> `stringResource(R.string.view_guide)`
- **Simulation Dashboard**:
    - "Hackathon Demo (4 Levels)" -> `stringResource(R.string.hackathon_demo)`
    - Numbered simulation buttons -> Use `stringResource(R.string.sim_safe)`, `R.string.sim_phishing`, etc.
- **Risk Gauge**:
    - "Risk Score" -> `stringResource(R.string.risk_score_label)`

#### [MODIFY] [strings.xml](file:///C:/Project/gram%20kavacha/app/src/main/res/values/strings.xml)
- Remove `translatable="false"` from keys like `analyzing_context`, `risk_monitoring_active`, `safety_manual_subtitle`, etc., to allow them to be translated.
- Ensure all keys used in `HomeScreen.kt` are defined here.

### ⚙️ Data Layer (:data)

#### [MODIFY] [RiskRepositoryImpl.kt](file:///C:/Project/gram%20kavacha/data/src/main/kotlin/org/gramkavach/data/RiskRepositoryImpl.kt)
- Double-check that the default language logic correctly picks up the system locale on the very first run.

## Verification Plan

### Manual Verification
1. Set the phone's system language to **Gujarati** or **Hindi**.
2. Reset the app data.
3. Verify that the **Meter Gauge** page shows everything (Risk Score, Status, Buttons, Cards) in the selected language.
4. Verify that the simulation buttons also show translated text.
