# Implementation Plan - 100% Deep Localization (Labels + Data)

The goal is to ensure that **every single word** in the app, including dynamic content like Alert History, Transaction Details, and Risk Reasons, is fully localized into the user's selected language. We will move from "Hardcoded Strings" to a "Resource Key" architecture.

## User Review Required

> [!IMPORTANT]
> To support 100% localization in Alert History, I will be changing how data is stored. Instead of storing "UPI Collect Request" in the database, we will store a key like `type_collect`. The app will then translate this key on-the-fly based on your current language setting.

## Proposed Changes

### 1. Architecture: Resource Key System

#### [MODIFY] [HybridRiskEngine.kt](file:///C:/Project/gram%20kavacha/ai/src/main/kotlin/org/gramkavach/ai/HybridRiskEngine.kt)
- Refactor the `reasons` list to return string resource keys instead of English sentences.
- Example: `"A screen-sharing... app is active"` -> `signal_remote_access`.

#### [MODIFY] [HomeViewModel.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/HomeViewModel.kt)
- Refactor `transactionType` in all simulation scenarios to use resource keys.
- Example: `"Bank Scam Call"` -> `type_bank_scam`.

---

### 2. Multi-Language Resource Expansion

#### [MODIFY] [strings.xml (All 7 Languages)](file:///C:/Project/gram%20kavacha/app/src/main/res/values/)
- Add localized translations for all **Risk Signals**:
    - `signal_remote_access`, `signal_accessibility`, `signal_unknown_call`, `signal_collect`, `signal_phishing`, `signal_qr`.
- Add localized translations for all **Transaction Types**:
    - `type_verified`, `type_phishing`, `type_collect`, `type_bank_scam`.

---

### 3. UI Content Resolution

#### [MODIFY] [MainActivity.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/MainActivity.kt)
- **`AlertHistory` Screen**: Update the card rendering to resolve both `alert.details` (Transaction Type) and `alert.reasons` from the resource system using `getResId()`.
- **`RiskAlert` Screen**: Ensure the "Risk Signals" list correctly resolves its keys into localized text.

---

### 4. Demo Feedback Optimization

#### [MODIFY] [MainActivity.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/MainActivity.kt)
- Fix the **Risk Gauge** color and labels to ensure they are consistent across all languages.
- Ensure the "System Protected" / "Risk Detected" status string is perfectly formatted in the header card.

## Verification Plan

### Manual Verification
- **The "Everything" Test**:
    1. Select **"ગુજરાતી" (Gujarati)**.
    2. Click **"Simulate UPI Collect"**.
    3. Verify that the Alert Title, Gauge Status, Table Factors, Reasons, and Buttons are all in Gujarati.
    4. Go to **History** and verify that the entry ("UPI Collect Request") is also in Gujarati.
- **Repeat for Hindi & Marathi**: Ensure consistency across all major target languages.
