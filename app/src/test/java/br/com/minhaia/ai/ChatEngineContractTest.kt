package br.com.minhaia.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ChatEngineContractTest {
    @Test
    fun defaultGenerationOptionsAreWithinSupportedBounds() {
        val options = GenerationOptions()

        assertEquals(128, options.maxOutputTokens)
        assertEquals(0.7f, options.temperature)
        assertEquals(0.9f, options.topP)
    }

    @Test
    fun acceptsDocumentedBoundaryValues() {
        GenerationOptions(maxOutputTokens = 1, temperature = 0.0f, topP = 0.0001f)
        GenerationOptions(maxOutputTokens = 512, temperature = 2.0f, topP = 1.0f)
    }

    @Test
    fun rejectsOutputTokenLimitsOutsideBounds() {
        assertThrows(IllegalArgumentException::class.java) {
            GenerationOptions(maxOutputTokens = 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            GenerationOptions(maxOutputTokens = 513)
        }
    }

    @Test
    fun rejectsNonFiniteOrOutOfRangeSamplingValues() {
        assertThrows(IllegalArgumentException::class.java) {
            GenerationOptions(temperature = Float.NaN)
        }
        assertThrows(IllegalArgumentException::class.java) {
            GenerationOptions(temperature = 2.01f)
        }
        assertThrows(IllegalArgumentException::class.java) {
            GenerationOptions(topP = Float.NaN)
        }
        assertThrows(IllegalArgumentException::class.java) {
            GenerationOptions(topP = 0.0f)
        }
        assertThrows(IllegalArgumentException::class.java) {
            GenerationOptions(topP = 1.01f)
        }
    }

    @Test
    fun rejectsBlankPromptMessages() {
        assertThrows(IllegalArgumentException::class.java) {
            PromptMessage(PromptRole.USER, "  ")
        }
    }
}
