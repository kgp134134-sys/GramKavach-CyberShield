# 🛡️ GramKavach

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2026%2B-green.svg?style=flat&logo=android)](https://developer.android.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/Version-0.4.0-orange.svg)](https://github.com/kgp134134-sys/GramKavach456/releases)

> **AI-powered digital financial safety assistant for preventing UPI fraud.**

---

## 📍 Table of Contents
- [📥 Download & Demo](#-download--demo)
- [🎯 Problem Statement](#-problem-statement)
- [💡 Proposed AI/ML Solution](#-proposed-aiml-solution)
- [🚀 Maverick Effect Alignment](#-maverick-effect-alignment)
- [🚀 Features](#-features)
- [🛠️ Tech Stack](#-tech-stack)
- [📐 Architecture](#-architecture)
- [⚙️ Installation](#-installation)
- [📱 In-App User Guide](#-in-app-user-guide)
- [📸 Screenshots](#-screenshots)
- [⚠️ ONNX Model Disclaimer](#-onnx-model-disclaimer)

---

## 📥 Download & Demo

**Get the latest hackathon-ready build here:**
[**Download GramKavach v0.4.0 APK**](https://github.com/kgp134134-sys/GramKavach456/releases/latest/download/app-release.apk)

> [!NOTE]
> **Installation Tip**: Since this is a **hackathon demo prototype** and not yet published on the Play Store, Android may show a "Play Protect" or "Unknown Developer" warning.
> - To install: Click **"More Details"** -> **"Install Anyway"**.
> - This is expected for side-loaded demo applications.

**Hackathon Highlights:**
- **Sanskriti UI**: Culturally resonant earthy theme (Saffron/Cream) with an animated rotating Rangoli pattern.
- **Smart Onboarding**: Personalized "Create My Safety Shield" flow with data stored locally.
- **Detailed Safety Rules**: High-impact "Do's and Don'ts" guide specifically for rural fraud scenarios.
- **Explainable AI**: Dynamic "Detection Analysis" table explaining risk scores.
- **Winner Dashboard**: Real-time pulsing risk gauge and safety status.

## 🎯 Problem Statement

Digital payment (UPI) adoption in Rural India has surged, but it has been accompanied by a massive rise in fraud cases (Fake SMS, Phishing links, Screen-sharing scams, and Lottery/Bill traps). Existing security solutions are largely **reactive** (alerting only after money is lost) and **English-centric**, leaving low-literacy users highly vulnerable.

## 💡 Proposed AI/ML Solution

GramKavach is a **100% On-Device, Pre-PIN Prevention System** that stops fraud **before** the transaction is authorized:
- **On-Device Threat Intelligence**: Uses local pattern-matching and lightweight ML models to analyze fake SMS, APK download links, and phishing URLs in real-time.
- **System Auto-Localization Engine**: Automatically adapts UI and Audio Warnings to the user's preferred language, removing onboarding friction for rural users.
- **Interactive Risk Gauge**: Converts complex risk signals into simple visual (Red/Orange/Green) and audio-first warnings for illiterate or rural users.

## 🚀 Maverick Effect Alignment

GramKavach honors the "Maverick Effect" legacy through:
- **Frugal & Scalable**: Operates with **zero server costs** and zero internet requirements for core detection (100% offline functionality).
- **Societal Impact**: Builds critical digital trust within the rural financial ecosystem, empowering the next billion users.
- **Mindful Innovation**: Transforms complex cyber threat detection into a simple, culturally resonant audio-visual experience.

## 🚀 Features

- **Sanskriti Visual Identity**: A culturally conscious UI featuring a warm earthy palette and animated Rangoli patterns for a traditional Indian feel.
- **On-Device Explainable AI**: Interactive risk scoring that provides a "Security Analysis Breakdown" for every check.
- **Detailed Safety Rules**: A dedicated "Must Read" guide covering specific scams like fake electricity bills and PM Yojana frauds.
- **Real-time Safety Overlay**: A pulsing floating badge that warns users during high-risk calls or payment requests.
- **Emergency Reporting**: Quick-dial shortcut to **1930 Cyber Cell** for immediate fraud reporting.
- **Privacy First**: 100% local processing; no transaction interception or private data collection.

## 🛠️ Tech Stack

- **Language**: Kotlin 2.1.0
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM / Clean Architecture
- **DI**: Hilt 2.57
- **Database**: Room (with destructive migration fallback)
- **Data**: DataStore, Coroutines, Flow
- **AI**: ONNX Runtime Mobile
- **Voice**: Bhashini AI Pipeline / Android TTS

## 📐 Architecture

GramKavach follows a strict **Clean Architecture** pattern. Below is the detailed structural overview of the system components and their interactions:

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

### 🚨 Pre-PIN Prevention Workflow

How GramKavach stops fraud before money leaves the account:

```mermaid
sequenceDiagram
    autonumber
    participant S as System (Monitoring)
    participant UC as Use Case (Domain)
    participant AI as AI Engine (Infra)
    participant V as Voice Assistant (Bhashini)
    participant UI as User Interface (Sanskriti)

    S->>UC: 📡 High-Risk Signal Detected
    UC->>AI: 🧠 Request Hybrid Assessment
    AI-->>UC: 📊 Risk Score: 85 (Critical)
    
    par Real-time Alerting
        UC->>UI: 🚨 Show "Critical" Overlay
        UC->>V: 🗣️ Trigger "Voice Warning" (Hindi)
    end

    UI->>UI: 🌀 Pulse Rangoli (Red State)
    V-->>UI: "Stop! This looks like a fraud..."
    
    Note over UI,V: Pre-PIN Prevention complete. Money is safe.
```

See [Architecture.md](docs/Architecture.md) and [Workflow.md](docs/Workflow.md) for more details.

## ⚙️ Installation

1. Open this directory in **Android Studio Ladybug** (2024.2.1) or newer.
2. Ensure you have **JDK 17** and **Android SDK Platform 35** installed.
3. Let Gradle sync, then run the `app` configuration on an **Android 8.0 (API 26)+** device.

### Bhashini Setup (Optional)
For voice synthesis, set the following in your global `gradle.properties`:
```properties
BHASHINI_USER_ID=your_user_id
BHASHINI_API_KEY=your_api_key
BHASHINI_BASE_URL=https://dhruva-api.bhashini.gov.in/
```
If credentials are unavailable, GramKavach automatically falls back to the device's default Text-to-Speech engine.

## 📱 In-App User Guide
GramKavach contains a built-in 4-step guide to help new users understand the safety features:

| 1. Real-time Monitoring | 2. Smart Risk Analysis |
| :--- | :--- |
| <img src="screenshots/guide1.jpg" width="250"/> | <img src="screenshots/guide2.jpg" width="250"/> |
| **3. Local Voice Alerts** | **4. Emergency Reporting** |
| <img src="screenshots/guide3.jpg" width="250"/> | <img src="screenshots/guide4.jpg" width="250"/> |

---

## 📸 Screenshots

Explore the GramKavach interface and dynamic safety alerts:

### 🛡️ Dashboard & Risk States
| Safe Status | Caution Alert | High Risk |
| :--- | :--- | :--- |
| <img src="screenshots/safe-status-dashboard.jpg" width="200"/> | <img src="screenshots/caution-dashboard.jpg" width="200"/> | <img src="screenshots/critical-dashboard.jpg" width="200"/> |

### 🚨 Real-time Alerts
| Warning Overlay | Moderate Alert | History Log |
| :--- | :--- | :--- |
| <img src="screenshots/critical-alert.jpg" width="200"/> | <img src="screenshots/moderate-alert.jpg" width="200"/> | <img src="screenshots/alert-history.jpg" width="200"/> |

### ⚙️ Settings & App Info
| My Profile | Settings | About GramKavach |
| :--- | :--- | :--- |
| <img src="screenshots/my-profile.jpg" width="200"/> | <img src="screenshots/settings.jpg" width="200"/> | <img src="screenshots/about-gramkavach.jpg" width="200"/> |

---

## ⚠️ ONNX Model Disclaimer

> [!WARNING]
> **Production Model Status**: `OnnxRiskModel` is a template for executing ONNX models. A real production-grade fraud model is not included as it requires training on representative, consented datasets. The current "Demo Mode" uses a hybrid rule-based risk engine for safe simulation.

## 🔮 Future Scope

- Validated and calibrated ONNX model deployment.
- Offline local language packs for low-connectivity regions.
- Encrypted exportable incident reports for law enforcement.
- Community-based usability testing in rural clusters.

## 👨‍💻 Developer

**Khushbu Prajapati** 

Solo Developer  
Government Engineering College, Patan

GitHub: https://github.com/kgp134134-sys

## 🙏 Acknowledgments

This project was built from my own idea, concept, planning, and hard work. AI tools helped with code generation, debugging, improvements, and documentation.

> 🤖 AI assisted during development. The idea, concept and project design are original.

## 📄 License

MIT — see [LICENSE](LICENSE).
