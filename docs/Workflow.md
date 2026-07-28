# Safety workflow

The core "Pre-PIN" prevention loop ensures that high-risk transactions are flagged before the user authorizes them.

sequenceDiagram
    autonumber
    participant C as Context Monitor
    participant E as Hybrid Engine
    participant U as Sanskriti UI
    participant S as Local Storage
    participant V as Voice Assistant

    C->>E: 📡 Permissioned risk signals
    E->>E: 🧠 Rules + ONNX ML score
    E->>U: 📊 Score, level, analysis
    
    alt High Risk Alert
        U->>U: 🚨 Show Critical Overlay
        U->>V: 🗣️ Play Regional Voice Warning
        E->>S: 💾 Store local alert history
    else Moderate Risk
        U->>U: ⚠️ Warn user; show safety tips
    else Safe System
        U->>U: ✅ System Protected badge
    end

### Guiding Principles

- **Advice, Not Blocking**: The application advises users; it must not claim that it can block or authorize third-party UPI transactions. 
- **Privacy First**: All processing is 100% on-device. No payment metadata or PINs are ever captured or uploaded.
- **Accessibility**: Voice guidance (via Bhashini) ensures that low-literacy users are protected through audio alerts in their preferred language.
