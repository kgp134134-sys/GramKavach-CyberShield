# Architecture: The Safety Fabric

GramKavach is built on a modular, domain-centric architecture that ensures scalability, testability, and a clear separation of concerns.

```mermaid
flowchart TD
    %% 3D STACK LAYERS
    subgraph L3 ["📱 LAYER 3: PRESENTATION (Sanskriti UI)"]
        direction TB
        UI(["Dashboard & Screens"])
        VM(["ViewModels"])
    end

    subgraph L2 ["🛡️ LAYER 2: DOMAIN (The Safety Core)"]
        direction TB
        UC(["Use Cases"])
        subgraph Contracts ["📜 Protocol Contracts"]
            RE(["RiskEngine"])
            VA(["VoiceAssistant"])
            RN(["RiskNotifier"])
            RR(["RiskRepository"])
        end
        UC -->|Calls| Contracts
    end

    subgraph L1 ["⚙️ LAYER 1: INFRASTRUCTURE (The Foundation)"]
        direction LR
        AI_Imp(["Hybrid Scorer"])
        V_Imp(["Voice Implementation"])
        M_Imp(["Signal Monitor"])
        D_Imp(["Room DB"])
    end

    %% Dependency Rules
    L3 ==>|Requests| L2
    L1 -.->|Fulfills| L2
    
    %% Styling
    classDef layer3 fill:#FFF9F0,stroke:#E65100,stroke-width:4px,color:#E65100
    classDef layer2 fill:#FFFDE7,stroke:#A04000,stroke-width:3px,color:#A04000
    classDef layer1 fill:#F1F8E9,stroke:#2E7D32,stroke-width:2px,color:#2E7D32
    
    class L3 layer3
    class L2 layer2
    class L1 layer1
```

## Data Signal Pipeline (ASCII Detail)

This diagram shows how a raw risk signal travels through the layers to become a user alert.

```text
[ HARDWARE/SYSTEM ]        [ INFRASTRUCTURE ]        [ DOMAIN ]             [ PRESENTATION ]
        │                          │                    │                         │
  ┌─────▼─────┐             ┌──────▼──────┐      ┌──────▼──────┐           ┌──────▼──────┐
  │ Monitoring│  =========> │  Signal Bus │ ===> │ Use Case    │ =========>│ Sanskriti   │
  │ Service   │  (context)  │  (filtering)│      │ Logic       │ (UI State)│ Dashboard   │
  └───────────┘             └─────────────┘      └──────┬──────┘           └──────┬──────┘
                                                        │                         │
                                                 ┌──────▼──────┐           ┌──────▼──────┐
                                                 │  Contracts  │           │ Bhashini    │
                                                 │ (Interfaces)│ <======== │ Voice Alert │
                                                 └─────────────┘           └─────────────┘
```

## Layer Breakdown
- **🛡️ Domain Layer (`:domain`)**: Pure Kotlin. The absolute source of truth. Contains `UseCases` that define *what* the system does.
- **📱 Presentation Layer (`:app`)**: Built with Jetpack Compose. Implements the "Sanskriti" design system and manages UI state.
- **⚙️ Infrastructure Layer**: Implementations of domain contracts. 
  - `:ai` handles ML (ONNX).
  - `:bhashini` handles linguistic diversity.
  - `:monitoring` watches for system-level fraud signals.

Sensitive information is not uploaded by this starter. Production telemetry must be opt-in, minimized, encrypted, and governed by a reviewed retention policy.
