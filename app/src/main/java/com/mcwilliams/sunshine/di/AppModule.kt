package com.mcwilliams.sunshine.di

import android.content.Context
import com.mcwilliams.sunshine.network.LocationApi
import com.mcwilliams.sunshine.network.WeatherApi
import com.mcwilliams.sunshine.ui.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module(includes = [NetworkModule::class])
class AppModule {
    @Provides
    @Singleton
    fun providesCurrentWeatherRepository(
        @ApplicationContext context: Context,
        weatherApi: WeatherApi,
        locationApi: LocationApi,
    ): WeatherRepository =
        WeatherRepository(context, weatherApi, locationApi)
}