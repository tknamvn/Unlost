# UNLOST APP - PROJECT AGENT RULES

Refer to [RULES.txt](file:///g:/NAM/Unlost/RULES.txt) for the primary strict guidelines.

### Mandatory Rules for AI Agents:
1. **Always read `RULES.txt` before writing or refactoring code.**
2. **Strict Line Limits:** Max 200 lines per file, max 50 lines per Composable.
3. **Architecture:** Clean Architecture + MVVM + UDF. Pure Kotlin Domain layer.
4. **Design System:** Use `core/designsystem` (or `ui/components` & theme tokens). No hardcoded colors/dimensions in UI features.
5. **Data & Security:** Room + SQLCipher on `Dispatchers.IO`. Offline-first.
6. **Voice & Audio Safety:** Always release audio resources in `onCleared()` / `DisposableEffect`.
