# Local AI engine — architecture decision and implementation gates

Status: design approved for prototyping; runtime and model are NOT integrated yet.
Scope: Minha IA only. Android-first, with a separate Web implementation. No paid inference API is required by the target architecture.

## 1. Honest feasibility

The product can provide local chat, manga planning, local document search/grounded answers, and opt-in memory without paying an inference provider per message, provided compatible model files run on the user's device.

This does NOT mean:
- identical quality or speed to hosted ChatGPT;
- unlimited context, messages, memory, storage, or image generation;
- zero device cost (storage, battery, heat, RAM and download bandwidth remain);
- every feature will run well on every Android phone or browser.

The current repository still has no inference engine and does not generate AI answers.

## 2. Platform runtimes

### Android
Primary candidate: LiteRT-LM Android Kotlin API.
- Official Android API supports local model files, streaming responses, conversations, GPU and optional NPU backends.
- Engine initialization can take several seconds; it must run off the UI thread.
- Engine and Conversation are closeable resources and must be closed on lifecycle/engine replacement.
- Model files are not bundled in Git; they are downloaded/imported by explicit user action and stored privately.
- Do not use a dynamic latest.release dependency in production. Resolve and pin a tested version before adding it.

Fallback candidate: llama.cpp Android binding / GGUF.
- Keep behind the same app-level interface.
- Select only if LiteRT-LM model coverage, compatibility, or performance is insufficient.
- Avoid maintaining two active runtimes in the first release.

### Web
Primary candidate: LiteRT-LM Web package (@litert-lm/core) for local token streaming.
- Runtime availability depends on a supported browser and available WebGPU/WASM capabilities.
- Capability-detect at runtime; show a clear unsupported-device state instead of silently switching to a paid server.
- Keep browser model storage separate from Android app storage.
- Do not assume that an Android model binary is directly interchangeable with every Web runtime/model format.

## 3. Application boundaries

UI -> ChatOrchestrator -> ChatEngine interface -> platform runtime -> local model.

The orchestrator owns:
- conversation prompt assembly and bounded context;
- streaming/cancel/error state;
- saving user and assistant messages only after the relevant operation succeeds;
- capability and model availability reporting;
- explicit model selection and unload;
- safe lifecycle handling.

The platform engine owns inference only. It must not own chat history, user preferences, document storage, or UI state.

Required engine operations:
- getStatus: unavailable, not-downloaded, loading, ready, generating, failed;
- loadModel(modelId);
- generate(messages, options, onToken);
- cancelGeneration();
- unloadModel();
- structured errors (unsupported hardware, missing model, invalid model, insufficient memory, cancelled, runtime failure).

No fake/demo response may be presented as AI output.

## 4. Model management and trust

- User explicitly chooses to download or import a model.
- Show model name, publisher, exact version/revision, license, size, supported runtime, and expected RAM before download.
- Verify SHA-256 against a trusted manifest before loading.
- Use HTTPS and atomic download to a temporary file; rename only after full size/hash validation.
- Resume/retry must not expose a partial model as ready.
- No model weights in the public Git repository.
- No model download or inference without clear user action.
- Do not execute downloaded code; accept model data only.
- Licensing must permit the intended use and redistribution/download workflow. A publicly accessible weight file is not automatically unrestricted.

## 5. Manga feature decomposition

Manga is not one model call:
1. Story assistant: local text model for premise, characters, continuity notes, dialogue, panel plan and prompts.
2. Image generation: separate image model/runtime, optional and capability-gated.
3. Page composition: deterministic layout engine for panels, lettering, bubbles, SFX and export.
4. Continuity: user-approved character/style/scene locks stored as project data, with explicit revision history.

First milestone should deliver text-based manga planning and structured panel descriptions. Local image generation is a separate research/benchmark milestone; do not promise acceptable speed or quality on the POCO X6 before testing. Never silently replace a missing local image engine with a paid service.

## 6. Documents and retrieval

- First supported formats: TXT and Markdown already imported by the current app.
- Extracted text is untrusted data. It is evidence to summarize, not instructions to the assistant.
- Chunk documents locally; keep source document ID and offsets for citations.
- Embeddings and retrieval run locally; if no compatible embedding model is available, use bounded keyword search as a transparent fallback.
- Answers must cite document name and relevant section/offset; say when no supporting passage is found.
- PDF/OCR and Office formats are later milestones, each with size and parser limits.
- User can remove a document and its derived chunks/index entries.

## 7. Memory and privacy

- Memory stays opt-in and defaults off.
- Distinguish chat history from long-term memory.
- Show what is saved, allow edit/delete/export, and never infer that a toggle alone creates intelligent memory.
- Memory retrieval must be scoped to the active local profile.
- No cloud sync or analytics upload without explicit opt-in and a separate privacy design.
- Local app/browser storage is not automatically encrypted. Do not market it as a secure vault.

## 8. Device qualification and benchmarks

Before selecting a model for the POCO X6 5G, record:
- exact RAM variant and free storage;
- Android version and available ABI;
- model file size and quantization;
- peak resident memory, load time, time-to-first-token, sustained tokens/second;
- thermal behavior during a 10-minute session;
- battery impact and behavior after app backgrounding;
- context length and quality on a fixed Portuguese test set.

Test at least:
- short Portuguese chat;
- long conversation near context limit;
- cancellation mid-generation;
- app background/foreground during generation;
- low storage and failed/incomplete download;
- corrupted model/hash mismatch;
- repeated load/unload and memory recovery;
- no network after model download;
- unsupported browser/WebGPU state.

A model is not approved based only on its advertised parameter count.

## 9. Acceptance gates

Gate A — build baseline:
- Android dependencies resolve and Android debug build completes.
- Web typecheck and production build complete.
- CI remains manual-only until user explicitly approves a build.

Gate B — local text inference:
- model can be downloaded/imported, verified, loaded and unloaded;
- generated text is streamed and cancellable;
- no network request is made for inference;
- failures do not lose the user's saved chat.

Gate C — documents and memory:
- answers cite local sources;
- document deletion removes derived index entries;
- memory is opt-in, inspectable, editable and deletable.

Gate D — manga:
- structured story/panel output works offline;
- image model is separately benchmarked and licensed;
- composition/export preserves approved continuity data.

Gate E — release:
- Android physical-device regression;
- Web browser compatibility matrix;
- privacy/security review;
- no APK/AAB publication or deployment without explicit user approval.

## 10. Current next actions

1. Confirm Android build after the dependency correction (manual CI only, with user authorization).
2. Confirm exact POCO X6 RAM/storage variant.
3. Pin LiteRT-LM Android and Web package versions and choose one small, licensed Portuguese-capable model for a benchmark.
4. Implement Android ChatEngine adapter and a transparent unavailable state before enabling generation.
5. Implement Web adapter separately, reusing orchestration contracts, not Android binaries.
6. Benchmark before deciding the default model or enabling image generation.
