package org.gramkavach.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "alerts", indices = [Index(value = ["createdAtEpochMs"])])
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val level: String,
    val reasons: String,
    val phoneNumber: String? = null,
    val details: String? = null,
    val createdAtEpochMs: Long,
)
