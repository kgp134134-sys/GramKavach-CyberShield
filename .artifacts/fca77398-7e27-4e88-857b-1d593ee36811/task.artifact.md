# Task: Detailed Clean Architecture & Sanskriti UI Implementation

- [x] **🛡️ Core (Domain)**
    - [x] Create `VoiceAssistant` interface in `:domain`
    - [x] Refine `RiskEngine` interface in `:domain`
    - [x] Update `AssessPaymentRiskUseCase` to orchestrate voice alerts
- [x] **⚙️ Infrastructure**
    - [x] Refine `HybridRiskEngine` in `:ai` (ONNX integration)
    - [x] Implement `VoiceAssistant` in `BhashiniTextToSpeech` (`:bhashini`)
    - [x] Ensure `RiskRepositoryImpl` (`:data`) is optimized
- [x] **📱 Presentation**
    - [x] Polish `GramKavachTheme` with Sanskriti palette
    - [x] Update `HomeScreen` with Rangoli background & pulsing gauge
    - [x] Connect `HomeViewModel` to updated Use Cases
- [x] **Verification**
    - [x] Verify Domain purity (no Android imports)
    - [x] Build and verify UI styling
