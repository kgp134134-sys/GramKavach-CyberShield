package org.gramkavach.core.logging

import android.util.Log

/**
 * Standardized logger for GramKavach.
 * In a real production app, this would use Timber or a similar library to ensure
 * logs are handled securely (e.g., stripped in release builds).
 */
object KavachLogger {
    private const val TAG = "GramKavach"

    fun d(message: String) {
        Log.d(TAG, "[DEBUG] $message")
    }

    fun i(message: String) {
        Log.i(TAG, "[INFO] $message")
    }

    fun w(message: String, throwable: Throwable? = null) {
        Log.w(TAG, "[WARN] $message", throwable)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, "[ERROR] $message", throwable)
    }
}
