# Phase 1 — interactive scaffold and first local persistence checkpoint

## Branch
`feature/phase1-scaffold`

## Added
- Android application skeleton using Kotlin built into AGP 9.x and Jetpack Compose.
- Web application skeleton using React, TypeScript and Vite with the React plugin configured.
- Navigation placeholders for Chat, Mangá, Documentos and Memória.
- Interactive chat composer on Android and Web.
- Web chat history persisted in IndexedDB on the same browser/device.
- IndexedDB schema version 1, message shape validation, maximum 300 messages, maximum 10,000 characters per message, and serialized writes.
- Web app reports load/save failures and does not overwrite existing local data when history loading fails.
- Explicit notice that no AI model is connected and no automatic assistant responses are generated.
- Build setup notes in `docs/BUILD_SETUP.md`.
- GitHub Actions workflow for Web build and Android debug build checks.

## Toolchain reviewed
- Android Gradle Plugin 9.4.0 stable line.
- Gradle 9.6.0 minimum for AGP 9.4.
- JDK 17.
- Compose BOM 2026.09.00.
- Kotlin Compose compiler plugin 2.4.21.
- Web: React 19, Vite 7 and TypeScript 5.8.

## Current behavior and limitations
- Web chat history is stored locally in IndexedDB; it is not synchronized and is not encrypted by this app.
- Browser data may be removed by clearing site data or browser storage.
- The Android chat still uses volatile in-memory state and loses messages when the process/session ends.
- No AI inference, model download, PDF import/OCR, image generation, or account/sync backend is implemented.
- No Android persistence/memory settings UI is implemented yet.

## Validation
- Relevant source files were written and fetched back from GitHub for inspection.
- Package JSON was parsed successfully in an earlier checkpoint.
- Source review checked the IndexedDB load-before-save gate, malformed-record rejection, message length/history bounds, and serialized writes.
- No npm install, TypeScript check, Vite build, browser IndexedDB test, Gradle sync/build, or POCO X6 5G runtime test has been executed in this session.
- The CI workflow file exists; no CI run result is confirmed in this checkpoint.

## Known setup gap
Gradle Wrapper scripts/JAR are not yet present. CI uses a pinned Gradle 9.6.0 installation. A local Android Studio sync/build and physical-device test are still required.

## Security and privacy notes
- No API keys or credentials are included.
- Android backup is disabled in the initial manifest.
- The public repository must contain no personal data, model weights, tokens or private documents.
- Web chat content remains in the browser's local origin storage. It is not sent to a server by the current UI, but same-origin scripts can access IndexedDB; this is not an encrypted vault.
- The public app is currently single-profile; do not use it for private multi-user data until account isolation and storage ownership are designed and tested.
