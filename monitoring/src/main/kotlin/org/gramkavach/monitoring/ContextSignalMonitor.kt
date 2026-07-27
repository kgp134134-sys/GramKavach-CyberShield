package org.gramkavach.monitoring

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.gramkavach.domain.model.PaymentContext

/** Event-driven facade: platform integrations publish only consented, minimal risk signals. */
class ContextSignalMonitor {
    private val context = MutableStateFlow(PaymentContext())
    fun observe(): Flow<PaymentContext> = context
    fun publish(next: PaymentContext) { context.value = next }
}
