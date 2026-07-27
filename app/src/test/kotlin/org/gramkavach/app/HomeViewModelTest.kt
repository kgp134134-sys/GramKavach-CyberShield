package org.gramkavach.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.gramkavach.ai.HybridRiskEngine
import org.gramkavach.domain.model.AlertRecord
import org.gramkavach.domain.model.UserSettings
import org.gramkavach.domain.repository.RiskRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

private class MainDispatcherRule(val dispatcher: StandardTestDispatcher = StandardTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) { kotlinx.coroutines.Dispatchers.setMain(dispatcher) }
    override fun finished(description: Description) { kotlinx.coroutines.Dispatchers.resetMain() }
}

class HomeViewModelTest {
    @get:org.junit.Rule val mainDispatcherRule = MainDispatcherRule()
    @Test fun `sample check records and exposes assessment`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeRiskRepository()
        val viewModel = HomeViewModel(HybridRiskEngine(), repository)
        viewModel.runSampleCheck(); advanceUntilIdle()
        assertEquals(40, viewModel.assessment.value?.score)
        assertEquals(1, repository.saved.size)
    }
}

private class FakeRiskRepository : RiskRepository {
    val saved = mutableListOf<AlertRecord>()
    override fun observeAlerts(): Flow<List<AlertRecord>> = flowOf(saved)
    override suspend fun saveAlert(alert: AlertRecord) { saved += alert }
    override fun observeSettings(): Flow<UserSettings> = flowOf(UserSettings())
    override suspend fun updateSettings(settings: UserSettings) = Unit
}
