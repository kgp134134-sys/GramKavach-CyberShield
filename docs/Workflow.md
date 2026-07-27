# Safety workflow

```mermaid
sequenceDiagram
  participant C as Context monitor
  participant E as Hybrid engine
  participant U as User interface
  participant S as Local storage
  C->>E: Permissioned risk signals
  E->>E: Rules + optional local ML score
  E->>U: Score, level, plain-language reasons
  alt High risk
    U->>U: Warn user; offer voice guidance
    E->>S: Store local alert history
  else Lower risk
    U->>U: Remind user to verify recipient and amount
  end
```

The application advises users; it must not claim that it can block or authorize third-party UPI transactions. Android capability and policy checks are required for any overlay, notification, accessibility, or call-state feature.
