# Safety workflow

The core "Pre-PIN" prevention loop ensures that high-risk transactions are flagged before the user authorizes them.

### Guiding Principles

- **Advice, Not Blocking**: The application advises users; it must not claim that it can block or authorize third-party UPI transactions. 
- **Privacy First**: All processing is 100% on-device. No payment metadata or PINs are ever captured or uploaded.
- **Accessibility**: Voice guidance (via Bhashini) ensures that low-literacy users are protected through audio alerts in their preferred language.
