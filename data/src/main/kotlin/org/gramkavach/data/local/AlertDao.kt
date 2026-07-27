package org.gramkavach.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY createdAtEpochMs DESC")
    fun observeAll(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity): Long

    @Query("DELETE FROM alerts WHERE id NOT IN (SELECT id FROM alerts ORDER BY createdAtEpochMs DESC LIMIT 100)")
    suspend fun cleanupOldAlerts()
}
