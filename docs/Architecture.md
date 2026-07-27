# Architecture

GramKavach uses a dependency direction that keeps UI and device-specific code outside the business model.

```mermaid
flowchart TB
  app[app: Compose navigation and DI host] --> domain[domain: models, contracts, use cases]
  data[data: Room and DataStore] --> domain
  ai[ai: hybrid scorer and ONNX adapter] --> domain
  monitoring[monitoring: consented signal facade] --> domain
  alerts[alerts: notification/message policy] --> domain
  bhashini[bhashini: regional voice adapter] --> domain
```

- `domain` is pure Kotlin business logic.
- `data` owns device persistence and implements repository contracts.
- `ai` is local-first. Rules remain available if an ML model cannot load.
- `monitoring` is event-driven; it must never poll aggressively or bypass Android permission boundaries.
- `app` owns screen composition and navigation.

Sensitive information is not uploaded by this starter. Production telemetry must be opt-in, minimized, encrypted, and governed by a reviewed retention policy.
