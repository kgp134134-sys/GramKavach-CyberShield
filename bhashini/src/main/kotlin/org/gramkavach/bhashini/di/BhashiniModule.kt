package org.gramkavach.bhashini.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.gramkavach.bhashini.BhashiniTextToSpeech
import org.gramkavach.bhashini.ResilientVoiceAlertSpeaker
import org.gramkavach.domain.usecase.VoiceAssistant
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BhashiniModule {

    @Provides
    @Singleton
    fun provideBhashiniTts(@ApplicationContext context: Context): BhashiniTextToSpeech {
        return BhashiniTextToSpeech(
            context = context,
            baseUrl = "https://dhruva-api.bhashini.gov.in/",
            userId = "", // Should be configured
            apiKey = ""  // Should be configured
        )
    }

    @Provides
    @Singleton
    fun provideVoiceAssistant(
        @ApplicationContext context: Context,
        remote: BhashiniTextToSpeech
    ): VoiceAssistant {
        return ResilientVoiceAlertSpeaker(context, remote)
    }
}
