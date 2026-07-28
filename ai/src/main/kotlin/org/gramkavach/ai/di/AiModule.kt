package org.gramkavach.ai.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.gramkavach.ai.HybridRiskEngine
import org.gramkavach.domain.usecase.RiskEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {
    @Binds
    @Singleton
    abstract fun bindRiskEngine(impl: HybridRiskEngine): RiskEngine
}
