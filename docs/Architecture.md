# Architecture

GramKavach is built on a modular, domain-centric architecture that ensures scalability, testability, and a clear separation of concerns.

```mermaid
flowchart TD
    subgraph RiskAssessment ["Risk assessment"]
        direction TB
        AIB(["AI bindings<br/>Hilt module<br/>[AiModule.kt]"])
        HRE{{"Hybrid risk engine<br/>RiskEngine implementation"}}
        OMA(["ONNX model adapter<br/>optional inference<br/>[OnnxRiskModel.kt]"])
        
        AIB -- binds --> HRE
        HRE -. optional inference .-> OMA
    end

    subgraph SystemMonitoring ["System monitoring"]
        direction TB
        MI(["Monitoring integration<br/>Android manifest"])
        RMS(["Risk monitoring services<br/>Android services"])
        RSB(["Risk signal bus<br/>shared signal boundary<br/>[RiskSignalBus.kt]"])
        
        MI -- declares --> RMS
        RMS -- publishes context --> RSB
    end

    subgraph AppPresentation ["App & presentation"]
        direction TB
        AAE(["Application & activity<br/>Android entry points"])
        HHS(["Home & history state<br/>Compose view models<br/>[HomeViewModel.kt]"])
        HSC(["Home screen<br/>Compose screen<br/>[HomeScreen.kt]"])
        
        AAE -- hosts --> HHS
        HHS -- renders state --> HSC
    end

    subgraph DomainContracts ["Domain contracts"]
        direction TB
        APR(["Assess payment risk<br/>domain use case"])
        REC(["Risk engine contract<br/>domain interface<br/>[RiskEngine.kt]"])
        RM(["Risk models<br/>domain models<br/>[RiskModels.kt]"])
        
        APR -- delegates scoring --> REC
        APR -- produces result --> RM
    end

    subgraph ResponseHistory ["Response & history"]
        direction TB
        RN(["Risk notifier<br/>alert implementation"])
        FSW(["Full-screen warning<br/>Android activity"])
        BTS(["Bhashini text to speech<br/>voice implementation"])
        OW(["Overlay warnings<br/>warning surface"])
        
        RR(["Risk repository<br/>domain repository<br/>implementation"])
        AD(["Alert DAO<br/>Room DAO<br/>[AlertDao.kt]"])
        AHB[("Alert history database<br/>Room database")]
        
        RN -- launches --> FSW
        RN -- speaks warning --> BTS
        RN -- shows overlay --> OW
        
        RR -- stores history --> AD
        AD -- queries --> AHB
    end

    %% Cross-layer connections
    HRE -- implements --> REC
    RSB -- submits signals --> APR
    HHS -- requests assessment --> APR
    RM -- high-risk result --> RN
    RM -- records event --> RR
    HHS -- reads history --> RR

    %% Styling
    classDef risk fill:#FFEBEE,stroke:#EF5350,stroke-width:2px,color:#B71C1C
    classDef monitor fill:#E8F5E9,stroke:#66BB6A,stroke-width:2px,color:#1B5E20
    classDef app fill:#E3F2FD,stroke:#42A5F5,stroke-width:2px,color:#0D47A1
    classDef domain fill:#FFF3E0,stroke:#FFA726,stroke-width:2px,color:#E65100
    classDef history fill:#F3E5F5,stroke:#AB47BC,stroke-width:2px,color:#4A148C

    class AIB,HRE,OMA risk
    class MI,RMS,RSB monitor
    class AAE,HHS,HSC app
    class APR,REC,RM domain
    class RN,FSW,BTS,OW,RR,AD,AHB history
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
