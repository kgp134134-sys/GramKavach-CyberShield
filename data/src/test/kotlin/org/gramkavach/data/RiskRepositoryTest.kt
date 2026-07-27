package org.gramkavach.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.gramkavach.data.local.AlertDao
import org.gramkavach.data.local.GramKavachDatabase
import org.gramkavach.domain.model.AlertRecord
import org.gramkavach.domain.model.RiskLevel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RiskRepositoryTest {
    private lateinit var database: GramKavachDatabase
    private lateinit var alertDao: AlertDao
    private lateinit var repository: RiskRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GramKavachDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        alertDao = database.alertDao()
        repository = RiskRepositoryImpl(alertDao, context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `save and observe alerts`() = runBlocking {
        val alert = AlertRecord(
            score = 75,
            level = RiskLevel.HIGH,
            reasons = listOf("Suspicious call"),
            phoneNumber = "+91 1234567890",
            createdAtEpochMs = System.currentTimeMillis()
        )
        
        repository.saveAlert(alert)
        val alerts = repository.observeAlerts().first()
        
        assertEquals(1, alerts.size)
        assertEquals(75, alerts[0].score)
        assertEquals("+91 1234567890", alerts[0].phoneNumber)
    }
}
