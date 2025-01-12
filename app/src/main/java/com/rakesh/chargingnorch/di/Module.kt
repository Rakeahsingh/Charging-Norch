package com.rakesh.chargingnorch.di

import android.app.Application
import com.rakesh.chargingnorch.preference.DefaultPreferences
import com.rakesh.chargingnorch.preference.Preferences
import com.rakesh.chargingnorch.preference.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {



    @Provides
    @Singleton
    fun providePreference(
        app: Application,
    ): Preferences {
        return DefaultPreferences(app.dataStore)
    }


}