package org.gramkavach.alerts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.gramkavach.alerts.RealRiskNotifier
import org.gramkavach.domain.usecase.RiskNotifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlertsModule {
    @Binds
    @Singleton
    abstract fun bindRiskNotifier(impl: RealRiskNotifier): RiskNotifier
}
