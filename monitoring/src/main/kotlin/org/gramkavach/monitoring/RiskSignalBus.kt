package org.gramkavach.monitoring

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.gramkavach.domain.model.PaymentContext

/** In-process event bus; events contain only risk flags, never screen text, call audio, PINs, or OTPs. */
object RiskSignalBus {
    private val events = MutableSharedFlow<PaymentContext>(extraBufferCapacity = 8)
    val signals = events.asSharedFlow()
    fun publish(signal: PaymentContext) { events.tryEmit(signal) }
}
