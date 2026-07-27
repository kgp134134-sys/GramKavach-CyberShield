package org.gramkavach.ai

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import org.gramkavach.core.logging.KavachLogger
import java.io.File
import java.nio.FloatBuffer

/** Executes a deployed on-device model with one [1, 6] float input and one scalar probability output. */
class OnnxRiskModel(private val modelFile: File) : AutoCloseable {
    private val environment = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    init {
        require(modelFile.isFile && (modelFile.length() > 0)) { "Risk model is missing: ${modelFile.absolutePath}" }
        KavachLogger.i("Initializing ONNX session with: ${modelFile.name}")
        session = environment.createSession(modelFile.absolutePath, OrtSession.SessionOptions())
    }

    fun probability(features: FloatArray): Float {
        require(features.size == 6) { "Expected 6 normalized risk features" }
        val inputName = session.inputNames.single()
        OnnxTensor.createTensor(environment, FloatBuffer.wrap(features), longArrayOf(1, 6)).use { input ->
            session.run(mapOf(inputName to input)).use { result ->
                val raw = result[0].value
                return when (raw) {
                    is FloatArray -> raw.first()
                    is Array<*> -> ((raw.first() as FloatArray).first())
                    else -> error("Unsupported ONNX output type: ${raw::class.java.name}")
                }.coerceIn(0f, 1f)
            }
        }
    }
    override fun close() { session.close() }
}
