# Implementation Plan - Detailed Clean Architecture & Sanskriti UI

This plan provides a comprehensive implementation of the Clean Architecture layers for GramKavach, ensuring strict separation of concerns and a high-quality "Sanskriti" UI experience.

## User Review Required

> [!IMPORTANT]
> - **Domain Purity**: All core logic will be in the `:domain` module using only Kotlin (no Android/Room/Compose).
> - **Infrastructure Decoupling**: We will use Dependency Inversion for AI (ONNX), Voice (Bhashini), and Monitoring.
> - **Sanskriti UI**: The UI will strictly follow the Saffron/Cream/Terracotta palette for a premium, culturally resonant feel.

## Proposed Changes

### 🛡️ Core (Domain) - `:domain`
Pure Kotlin implementation.

#### [NEW] [VoiceAssistant.kt](file:///C:/Project/gram%20kavacha/domain/src/main/kotlin/org/gramkavach/domain/usecase/VoiceAssistant.kt)
- `interface VoiceAssistant`: Methods for `speak(text: String, language: String)` and `stop()`.

#### [MODIFY] [RiskEngine.kt](file:///C:/Project/gram%20kavacha/domain/src/main/kotlin/org/gramkavach/domain/usecase/RiskEngine.kt)
- Clean up the `interface RiskEngine` to be the single source of risk scoring.

#### [MODIFY] [AssessPaymentRiskUseCase.kt](file:///C:/Project/gram%20kavacha/domain/src/main/kotlin/org/gramkavach/domain/usecase/AssessPaymentRiskUseCase.kt)
- Add logic to trigger `VoiceAssistant` alerts if risk exceeds a threshold.

---

### ⚙️ Infrastructure - `:ai`, `:data`, `:bhashini`
Implementation of domain interfaces using Android/Library tools.

#### [MODIFY] [HybridRiskEngine.kt](file:///C:/Project/gram%20kavacha/ai/src/main/kotlin/org/gramkavach/ai/HybridRiskEngine.kt)
- Implement `RiskEngine`.
- Integrate `OnnxRiskModel` for data-driven scoring + existing rules for robust fallback.

#### [MODIFY] [BhashiniTextToSpeech.kt](file:///C:/Project/gram%20kavacha/bhashini/src/main/kotlin/org/gramkavach/bhashini/BhashiniTextToSpeech.kt)
- Implement `VoiceAssistant`.
- Handle API key authentication and fallback to Android TTS if offline.

---

### 📱 Presentation - `:app`
Jetpack Compose + Sanskriti Theme.

#### [MODIFY] [Theme.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/ui/theme/Theme.kt)
- Refine `GramKavachTheme` to ensure consistency across all screens.

#### [MODIFY] [HomeScreen.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/ui/screens/HomeScreen.kt)
- Implement a "Safety Dashboard" with a pulsing risk gauge.
- Use the **SaffronDeep** for primary actions and **CreamWarm** for backgrounds.
- Add an animated **Rangoli Pattern** background for the Sanskriti feel.

#### [MODIFY] [HomeViewModel.kt](file:///C:/Project/gram%20kavacha/app/src/main/kotlin/org/gramkavach/app/ui/viewmodels/HomeViewModel.kt)
- State management for the risk assessment flow.
- Direct invocation of `AssessPaymentRiskUseCase`.

## Verification Plan

### Automated Tests
- `gradlew :domain:test`: Verify use case logic.
- `gradlew :ai:test`: Verify hybrid scoring logic.

### Manual Verification
- **UI Check**: Verify the Saffron/Cream color combo looks premium on physical devices.
- **Voice Check**: Trigger a High Risk alert and confirm the Bhashini voice guidance starts.
- **Architecture Check**: Verify no `:domain` file imports `android.*` or `androidx.compose.*`.
