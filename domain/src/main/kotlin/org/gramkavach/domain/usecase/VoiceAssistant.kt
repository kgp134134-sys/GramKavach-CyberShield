package org.gramkavach.domain.usecase

/**
 * Interface for regional voice guidance (TTS).
 * Decouples the domain from specific implementations like Bhashini or Android TTS.
 */
interface VoiceAssistant {
    /** Speak the given [text] in the specified [languageTag] (e.g., "en", "hi"). */
    suspend fun speak(text: String, languageTag: String): Result<Unit>
    
    /** Stop any ongoing speech. */
    fun stop()
}
