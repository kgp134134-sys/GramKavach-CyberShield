# Implementation Plan - Architecture Alignment with README.md

Refactor the Risk Assessment workflow to strictly follow Clean Architecture as described in the project's [README.md](file:///C:/Project/gram kavacha/README.md). This involves moving orchestration logic from the `monitoring` layer into a concrete Use Case in the `domain` layer.

## User Review Required

> [!IMPORTANT]
> The current `AssessPaymentRiskUseCase` (interface) will be renamed to `RiskEngine` to better reflect its role as a service provider. A new concrete `AssessPaymentRiskUseCase` (class) will be created in the `domain` layer to handle the workflow.

## Proposed Changes

### [domain] Core Abstractions & Use Case

#### [MODIFY] [AssessPaymentRiskUseCase.kt](file:///C:/Project/gram kavacha/domain/src/main/kotlin/org/gramkavach/domain/usecase/AssessPaymentRiskUseCase.kt)
- Rename interface `AssessPaymentRiskUseCase` to `RiskEngine`.
- Create a concrete class `AssessPaymentRiskUseCase` that orchestrates `RiskEngine` and `RiskRepository`.

#### [MODIFY] [RiskModels.kt](file:///C:/Project/gram kavacha/domain/src/main/kotlin/org/gramkavach/domain/model/RiskModels.kt)
- Ensure models are robust and independent of implementation details.

### [ai] Implementation Layer

#### [MODIFY] [HybridRiskEngine.kt](file:///C:/Project/gram kavacha/ai/src/main/kotlin/org/gramkavach/ai/HybridRiskEngine.kt)
- Update to implement the renamed `RiskEngine` interface.

### [monitoring] Infrastructure Layer

#### [MODIFY] [RiskMonitoringService.kt](file:///C:/Project/gram kavacha/monitoring/src/main/kotlin/org/gramkavach/monitoring/RiskMonitoringService.kt)
- Update to call the new concrete `AssessPaymentRiskUseCase`.
- Remove manual `RiskRepository` saving logic, as it will be handled by the Use Case.

### [app] Dependency Injection & UI

#### [MODIFY] [AiModule.kt](file:///C:/Project/gram kavacha/ai/src/main/kotlin/org/gramkavach/ai/di/AiModule.kt)
- Update Hilt bindings for the renamed `RiskEngine`.

#### [MODIFY] [OverlayController.kt](file:///C:/Project/gram kavacha/alerts/src/main/kotlin/org/gramkavach/alerts/OverlayController.kt)
- Change the overlay shape from a pill (oval) to a rounded rectangle (`cornerRadius` reduced to 12f).
- Ensure the background rectangle uses bold colors corresponding to the risk level.

## Verification Plan

### Automated Tests
- Run `HybridRiskEngineTest`.
- Create a new `AssessPaymentRiskUseCaseTest` in `:domain` to verify the orchestration logic (saving to repo, etc.).

### Manual Verification
- Deploy the app and verify that risk assessments are still performed and saved to the history log.
- Ensure the floating overlay still appears when a risk is detected outside the app.
