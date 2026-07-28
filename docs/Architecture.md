# Architecture

GramKavach uses a dependency direction that keeps UI and device-specific code outside the business model.

```mermaid
flowchart TB
  app[app: Compose navigation and DI host]
  domain[domain: models, contracts, use cases]
  data[data: Room and DataStore]
  ai[ai: hybrid scorer and ONNX adapter]
  monitoring[monitoring: consented signal facade]
  alerts[alerts: notification/message policy]
  bhashini[bhashini: regional voice adapter]

  app --> domain
  data --> domain
  ai --> domain
  monitoring --> domain
  alerts --> domain
  bhashini --> domain

  %% Styling
  style app fill:#E1F5FE,stroke:#01579B,stroke-width:2px
  style domain fill:#FFF9C4,stroke:#FBC02D,stroke-width:2px
  style data fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px
  style ai fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px
  style monitoring fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px
  style alerts fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px
  style bhashini fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px
```

- `domain` is pure Kotlin business logic.
- `data` owns device persistence and implements repository contracts.
- `ai` is local-first. Rules remain available if an ML model cannot load.
- `monitoring` is event-driven; it must never poll aggressively or bypass Android permission boundaries.
- `app` owns screen composition and navigation.

Sensitive information is not uploaded by this starter. Production telemetry must be opt-in, minimized, encrypted, and governed by a reviewed retention policy.
