package org.gramkavach.ai

import kotlinx.coroutines.test.runTest
import org.gramkavach.domain.model.PaymentContext
import org.gramkavach.domain.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class HybridRiskEngineTest {
    @Test fun `remote access and collect request are critical`() = runTest {
        val result = HybridRiskEngine()(PaymentContext(remoteAccessActive = true, collectRequest = true))
        assertEquals(RiskLevel.CRITICAL, result.level)
    }
    @Test fun `no signals returns safe assessment`() = runTest {
        val result = HybridRiskEngine()(PaymentContext())
        assertEquals(RiskLevel.SAFE, result.level)
        assertEquals(0, result.score)
    }
}
