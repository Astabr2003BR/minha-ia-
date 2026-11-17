# Phase 1 — scaffold and interactive UI checkpoint

## Branch
`feature/phase1-scaffold`

## Added
- Android application skeleton using Kotlin built into AGP 9.x and Jetpack Compose.
- Web application skeleton using React, TypeScript and Vite with the React plugin configured.
- Navigation placeholders for Chat, Mangá, Documentos and Memória.
- Interactive chat composer on Android and Web. Submitted user messages are shown in current process/session state.
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

## Not implemented
- Local text or image inference.
- Persistent chat history or memory.
- PDF import, OCR or document parsing.
- Model download and integrity verification.
- Synchronization, accounts or backend.
- APK/AAB release or website deployment.

## Validation
- Relevant source files were written and fetched back from GitHub for inspection.
- Package JSON was parsed and the web build tools were moved to development dependencies.
- No Gradle sync/build, npm install, TypeScript check, Vite build, browser test or POCO X6 5G runtime test has been executed in this session.
- The CI workflow file was committed, but no run/status result was returned at the time of this checkpoint.

## Known setup gap
Gradle Wrapper scripts/JAR are not yet present. CI uses a pinned Gradle 9.6.0 installation. A local Android Studio sync/build and physical-device test are still required.

## Security notes
- No API keys or credentials are included.
- Android backup is disabled in the initial manifest.
- The public repository must contain no personal data, model weights, tokens or private documents.
- Chat messages are held only in volatile UI state; no cloud transmission or persistent storage is implemented.
