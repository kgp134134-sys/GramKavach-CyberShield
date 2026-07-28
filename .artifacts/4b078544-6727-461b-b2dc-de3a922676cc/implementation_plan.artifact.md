# Implementation Plan - Architecture Improvements for Risk Assessment

Refactor the `AssessPaymentRiskUseCase` and `HybridRiskEngine` to improve scalability, testability, and error handling. The current implementation is monolithic and uses hardcoded scoring logic, which makes it difficult to maintain and extend with new risk signals.

## User Review Required

> [!IMPORTANT]
> The return type of `AssessPaymentRiskUseCase` will change from `RiskAssessment` to `Result<RiskAssessment>`. This will require updates in the `:app` (ViewModels), `:alerts`, and `:monitoring` modules.

## Proposed Changes

### [domain] Core Abstractions

#### [MODIFY] [AssessPaymentRiskUseCase.kt](file:///C:/Project/gram kavacha/domain/src/main/kotlin/org/gramkavach/domain/usecase/AssessPaymentRiskUseCase.kt)
- Update the interface to return `Result<RiskAssessment>`.

#### [NEW] [RiskAnalyzer.kt](file:///C:/Project/gram kavacha/domain/src/main/kotlin/org/gramkavach/domain/usecase/RiskAnalyzer.kt)
- Define `RiskAnalyzer` interface to allow modular risk detection (e.g., `AccessibilityRiskAnalyzer`, `RemoteAccessAnalyzer`).

#### [MODIFY] [RiskModels.kt](file:///C:/Project/gram kavacha/domain/src/main/kotlin/org/gramkavach/domain/model/RiskModels.kt)
- Remove `System.currentTimeMillis()` from `RiskAssessment` default constructor.
- Introduce `RiskResult` sealed class or improve `RiskLevel` usage.

### [ai] Implementation Refactoring

#### [MODIFY] [HybridRiskEngine.kt](file:///C:/Project/gram kavacha/ai/src/main/kotlin/org/gramkavach/ai/HybridRiskEngine.kt)
- Refactor to orchestrate multiple `RiskAnalyzer` implementations.
- Implement the new `AssessPaymentRiskUseCase` signature.

#### [NEW] Analyzers in `:ai` module
- Create specialized analyzers for different risk factors (Accessibility, Remote Access, UPI Context).

### [app], [alerts], [monitoring] Integration

#### [MODIFY] [HomeViewModel.kt](file:///C:/Project/gram kavacha/app/src/main/kotlin/org/gramkavach/app/HomeViewModel.kt)
- Handle `Result<RiskAssessment>` from the use case.

#### [MODIFY] [OverlayController.kt](file:///C:/Project/gram kavacha/alerts/src/main/kotlin/org/gramkavach/alerts/OverlayController.kt)
- Update to handle the new use case return type if it calls it directly, or update call sites.

## Verification Plan

### Automated Tests
- Run existing `HybridRiskEngineTest`.
- Add unit tests for new `RiskAnalyzer` implementations.
- Verify `AssessPaymentRiskUseCase` mocking in tests.

### Manual Verification
- Deploy the app and trigger different risk scenarios (e.g., enable a simulated remote access flag).
- Verify that alerts still show up correctly with the new architecture.
