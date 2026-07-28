# Task: Complete Language Transition Fix

- [ ] **📐 Strings Resource Update**
    - [ ] Remove `translatable="false"` from key strings in `res/values/strings.xml`
    - [ ] Ensure all labels used in the Dashboard are represented as strings
- [ ] **📱 Presentation Layer (HomeScreen) Update**
    - [ ] Replace all hardcoded English strings in `HomeScreen.kt` with `stringResource` calls
- [ ] **📱 Presentation Layer (HomeViewModel) Update**
    - [ ] Ensure simulation names and reasons are pulled from resources
- [ ] **Verification**
    - [ ] Run app in Hindi/Gujarati and verify the Meter Gauge page is fully translated
