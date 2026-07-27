# Phase 3 production-readiness changes

- Monitoring now uses Hilt-owned dependencies and one cancellable signal collector, avoiding duplicate database instances and duplicate alert writes.
- Alert history has a time index and an explicit Room `1 -> 2` migration.
- Notification publishing checks runtime notification permission on Android 13+.
- Bhashini failure falls back to Android TextToSpeech and reports a user-readable error only if both engines fail.
- Unit tests cover safe and critical risk scoring plus the Home ViewModel persistence path. The Android instrumentation test checks the launch/navigation shell.

## Verification status

No Gradle/Android SDK runtime was available in this workspace and Gradle could not be downloaded because of the host TLS configuration. Source changes were reviewed statically only; run the commands in the README before a release.
