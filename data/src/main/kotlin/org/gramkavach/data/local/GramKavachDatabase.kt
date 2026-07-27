package org.gramkavach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AlertEntity::class], version = 3, exportSchema = true)
abstract class GramKavachDatabase : RoomDatabase() { abstract fun alertDao(): AlertDao }
