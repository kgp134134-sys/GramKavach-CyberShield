package org.gramkavach.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.gramkavach.data.RiskRepositoryImpl
import org.gramkavach.data.local.AlertDao
import org.gramkavach.data.local.GramKavachDatabase
import org.gramkavach.domain.repository.RiskRepository

@Module @InstallIn(SingletonComponent::class)
object DataModule {
    @Provides @Singleton fun database(@ApplicationContext context: Context): GramKavachDatabase = Room.databaseBuilder(context, GramKavachDatabase::class.java, "gram-kavach.db")
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .fallbackToDestructiveMigration()
        .build()
    @Provides fun alertDao(database: GramKavachDatabase): AlertDao = database.alertDao()
    @Provides @Singleton fun repository(dao: AlertDao, @ApplicationContext context: Context): RiskRepository = RiskRepositoryImpl(dao, context)
}

private val MIGRATION_1_2 = object : Migration(1, 2) { override fun migrate(database: SupportSQLiteDatabase) { database.execSQL("CREATE INDEX IF NOT EXISTS index_alerts_createdAtEpochMs ON alerts(createdAtEpochMs)") } }

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE alerts ADD COLUMN phoneNumber TEXT")
        database.execSQL("ALTER TABLE alerts ADD COLUMN details TEXT")
    }
}
