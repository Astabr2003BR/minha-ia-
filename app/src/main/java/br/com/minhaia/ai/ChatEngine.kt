package br.com.minhaia.ai

/**
 * Platform-independent contract for local text inference.
 *
 * Implementations must own their runtime resources on one serialized worker.
 * They must never perform model initialization or token generation on the UI thread.
 * No implementation may silently fall back to a remote inference service.
 */
enum class EngineState {
    UNAVAILABLE,
    NOT_DOWNLOADED,
    LOADING,
    READY,
    GENERATING,
    FAILED
}

data class EngineSnapshot(
    val state: EngineState,
    val modelId: String? = null,
    val detail: String? = null
)

enum class PromptRole {
    SYSTEM,
    USER,
    ASSISTANT
}

data class PromptMessage(
    val role: PromptRole,
    val content: String
) {
    init {
        require(content.isNotBlank()) { "Prompt content cannot be blank." }
    }
}

/**
 * Conservative generation limits for the first on-device benchmark.
 * These values limit output only; the runtime adapter must separately bound
 * the assembled input/context before sending it to the model.
 */
data class GenerationOptions(
    val maxOutputTokens: Int = 128,
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f
) {
    init {
        require(maxOutputTokens in 1..512) {
            "maxOutputTokens must be between 1 and 512."
        }
        require(temperature.isFinite() && temperature in 0.0f..2.0f) {
            "temperature must be finite and between 0.0 and 2.0."
        }
        require(topP.isFinite() && topP > 0.0f && topP <= 1.0f) {
            "topP must be finite and greater than 0.0, up to 1.0."
        }
    }
}

enum class EngineFailure {
    UNSUPPORTED_DEVICE,
    MODEL_NOT_FOUND,
    INVALID_MODEL,
    INSUFFICIENT_MEMORY,
    INSUFFICIENT_STORAGE,
    DOWNLOAD_FAILED,
    CHECKSUM_MISMATCH,
    CANCELLED,
    RUNTIME_FAILURE
}

class ChatEngineException(
    val reason: EngineFailure,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Local inference boundary consumed by the Android chat orchestrator.
 *
 * Contract:
 * - Calls that mutate or use the runtime must be serialized by the implementation.
 * - loadModel must initialize off the main thread and publish READY only after success.
 * - generate emits incremental text through onToken and returns only after completion.
 * - coroutine cancellation and cancelGeneration must stop active generation safely.
 * - unloadModel releases model/runtime memory and is safe when no model is loaded.
 * - errors must be typed; partial output must never be saved as a completed assistant turn.
 */
interface ChatEngine {
    fun snapshot(): EngineSnapshot

    suspend fun loadModel(modelId: String)

    suspend fun generate(
        messages: List<PromptMessage>,
        options: GenerationOptions = GenerationOptions(),
        onToken: (String) -> Unit
    )

    suspend fun cancelGeneration()

    suspend fun unloadModel()
}
