# Walkthrough - Auto System Language Support

I have enabled automatic regional language detection in GramKavach. This ensures that the app greets the user in their preferred tongue right from the start.

## Features Added

### 🌍 Smart Locale Detection
- **Native Experience**: The app now checks the device's system language during the first launch.
- **Supported Mapping**: If the device is set to **Hindi (hi)**, **Marathi (mr)**, **Bengali (bn)**, **Gujarati (gu)**, **Tamil (ta)**, or **Telugu (te)**, GramKavach will automatically switch all UI and Voice components to that language.
- **Intelligent Fallback**: If the system language is not one of the supported regional languages, it gracefully defaults to **English**, ensuring the app remains usable for everyone.

## Verification Results

### ⚙️ Logic Check (:data)
- Updated `RiskRepositoryImpl.kt` to dynamically calculate the `defaultLanguage` using `java.util.Locale.getDefault().language`.
- Verified that existing user choices (manual language selection in settings) still take priority over the system default, respecting user intent.

> [!TIP]
> To test this, change your phone's system language to **Hindi** and reset GramKavach. You'll see the magic happen!

---

### Final Check
- [x] Auto-detection logic implemented
- [x] Fallback safety ensured
- [x] Settings persistence maintained
