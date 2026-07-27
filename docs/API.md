# API contracts

## Local domain API

`AssessPaymentRiskUseCase` accepts `PaymentContext` and returns `RiskAssessment`:

```kotlin
val assessment = riskEngine(PaymentContext(collectRequest = true))
```

Scores range from 0 to 100 and map to `SAFE`, `CAUTION`, `HIGH`, or `CRITICAL`. Reasons are designed for user-facing explanations.

## Bhashini adapter

`BhashiniTextToSpeech.speak(message, languageTag)` calls the authenticated Bhashini pipeline endpoint and plays the returned PCM audio locally. It needs account-issued credentials and an account-enabled TTS pipeline; credentials belong in user Gradle properties, never source control.

## Model adapter

`OnnxRiskModel` is the boundary for an evaluated `risk_model.onnx` asset. Define and version its feature vector, output calibration, model provenance, and rollback strategy before enabling it for users.
