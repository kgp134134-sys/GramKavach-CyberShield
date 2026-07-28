# Implementation Plan - Auto-transition to System Language

This plan enables GramKavach to automatically detect the user's device language and apply it as the default app language if it's one of our supported regional languages (Hindi, Gujarati, Marathi, etc.).

## Proposed Changes

### ⚙️ Data Layer (:data)

#### [MODIFY] [RiskRepositoryImpl.kt](file:///C:/Project/gram%20kavacha/data/src/main/kotlin/org/gramkavach/data/RiskRepositoryImpl.kt)
- Update `observeSettings()` logic:
    - Instead of hardcoding `"en"` as the default, detect the system locale using `Locale.getDefault().language`.
    - Map the detected language to our supported list (`hi`, `mr`, `bn`, `gu`, `ta`, `te`).
    - Use "en" as the fallback if the system language is not supported.

## Verification Plan

### Manual Verification
- **Test System Language**:
    1. Set the phone's system language to **Hindi**.
    2. Clear app data or reset the user in settings.
    3. Open the app and verify that the onboarding and dashboard appear in **Hindi** automatically.
- **Test Fallback**:
    1. Set the phone's system language to something unsupported (e.g., French).
    2. Reset the app.
    3. Verify it defaults to **English**.
