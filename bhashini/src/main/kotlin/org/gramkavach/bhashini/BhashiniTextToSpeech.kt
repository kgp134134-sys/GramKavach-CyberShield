package org.gramkavach.bhashini

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.Base64
import java.util.concurrent.TimeUnit
import java.util.Locale
import kotlin.coroutines.resume

import org.gramkavach.domain.usecase.VoiceAssistant

/** Bhashini pipeline-inference client. It deliberately receives credentials at construction time. */
class BhashiniTextToSpeech(
    private val context: Context,
    baseUrl: String,
    private val userId: String,
    private val apiKey: String,
) {
    private val service = Retrofit.Builder().baseUrl(baseUrl.ensureSlash())
        .client(OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(25, TimeUnit.SECONDS).build())
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
        .build().create(BhashiniApi::class.java)

    suspend fun speak(text: String, languageTag: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            require(userId.isNotBlank() && apiKey.isNotBlank()) { "Bhashini credentials are not configured." }
            val language = languageTag.substringBefore('-')
            val response = service.infer(
                userId, apiKey,
                BhashiniRequest(
                    pipelineTasks = listOf(PipelineTask("tts", TtsConfig(language, "female"))),
                    inputData = InputData(listOf(TtsInput(text)))
                )
            )
            val audio = response.pipelineResponse.firstOrNull()?.audio?.firstOrNull()?.audioContent
                ?: error("Bhashini returned no audio content")
            playPcm(Base64.getDecoder().decode(audio))
        }
    }

    private fun playPcm(bytes: ByteArray) {
        val rate = 22_050
        val track = AudioTrack.Builder().setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY).build())
            .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(rate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
            .setBufferSizeInBytes(bytes.size).setTransferMode(AudioTrack.MODE_STATIC).build()
        track.write(bytes, 0, bytes.size); track.play()
    }
    private fun String.ensureSlash() = if (endsWith('/')) this else "$this/"
}

/** Uses Bhashini when configured, and Android's installed voice engine when it is unavailable. */
class ResilientVoiceAlertSpeaker(
    private val context: Context,
    private val remote: BhashiniTextToSpeech,
) : VoiceAssistant {
    private var localTts: AndroidTextToSpeech? = null

    init {
        // Pre-warm the local engine
        localTts = AndroidTextToSpeech(context)
    }

    override suspend fun speak(text: String, languageTag: String): Result<Unit> {
        val normalizedTag = when(languageTag.lowercase()) {
            "en" -> "en-IN"
            "hi" -> "hi-IN"
            "mr" -> "mr-IN"
            "bn" -> "bn-IN"
            "ta" -> "ta-IN"
            "te" -> "te-IN"
            "gu" -> "gu-IN"
            else -> languageTag
        }
        return remote.speak(text, normalizedTag).recoverCatching { 
            (localTts ?: AndroidTextToSpeech(context)).speak(text, normalizedTag).getOrThrow() 
        }
    }

    override fun stop() {
        localTts?.stop()
    }
}

private class AndroidTextToSpeech(context: Context) {
    private var engine: TextToSpeech? = null
    private var isInitialized = false
    private var initResult: Int? = null

    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            initResult = status
            isInitialized = true
        }
    }

    suspend fun speak(text: String, languageTag: String): Result<Unit> = withContext(Dispatchers.Main) {
        // Wait for initialization if needed (up to 2 seconds)
        var count = 0
        while (!isInitialized && count < 20) {
            kotlinx.coroutines.delay(100)
            count++
        }

        if (initResult != TextToSpeech.SUCCESS) {
            return@withContext Result.failure(IllegalStateException("Android TTS not ready"))
        }

        suspendCancellableCoroutine { continuation ->
            val locale = Locale.forLanguageTag(languageTag)
            val languageStatus = engine?.setLanguage(locale)
            
            if (languageStatus == TextToSpeech.LANG_MISSING_DATA || languageStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                if (continuation.isActive) continuation.resume(Result.failure(IllegalStateException("Voice data is unavailable for $languageTag")))
            } else {
                val utteranceId = "gramkavach-${System.nanoTime()}"
                engine?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) = Unit
                    override fun onDone(utteranceId: String) { if (continuation.isActive) continuation.resume(Result.success(Unit)) }
                    @Deprecated("Deprecated in Java") override fun onError(utteranceId: String) { if (continuation.isActive) continuation.resume(Result.failure(IllegalStateException("Android TTS failed"))) }
                })
                if (engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId) == TextToSpeech.ERROR) { 
                    if (continuation.isActive) continuation.resume(Result.failure(IllegalStateException("Android TTS could not start"))) 
                }
            }
        }
    }

    fun stop() {
        engine?.stop()
    }
}

interface BhashiniApi { @POST("services/inference/pipeline") suspend fun infer(@Header("userID") userId: String, @Header("ulcaApiKey") key: String, @Body request: BhashiniRequest): BhashiniResponse }
@JsonClass(generateAdapter = true) data class BhashiniRequest(val pipelineTasks: List<PipelineTask>, val inputData: InputData)
@JsonClass(generateAdapter = true) data class PipelineTask(val taskType: String, val config: TtsConfig)
@JsonClass(generateAdapter = true) data class TtsConfig(val language: String, val gender: String)
@JsonClass(generateAdapter = true) data class InputData(val input: List<TtsInput>)
@JsonClass(generateAdapter = true) data class TtsInput(val source: String)
@JsonClass(generateAdapter = true) data class BhashiniResponse(val pipelineResponse: List<PipelineResponse>)
@JsonClass(generateAdapter = true) data class PipelineResponse(val audio: List<AudioOutput>? = null)
@JsonClass(generateAdapter = true) data class AudioOutput(val audioContent: String)
