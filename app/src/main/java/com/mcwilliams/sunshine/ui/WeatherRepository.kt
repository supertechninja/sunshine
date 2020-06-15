package com.mcwilliams.sunshine.ui

import android.content.Context
import android.content.SharedPreferences
import com.mcwilliams.sunshine.R
import com.mcwilliams.sunshine.network.LocationApi
import com.mcwilliams.sunshine.network.WeatherApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    context: Context,
    private val weatherApi: WeatherApi,
    private val locationApi: LocationApi
) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    suspend fun getWeather(search: String): com.mcwilliams.sunshine.model.allweatherdata.WeatherData {
        val locations = preferences.getStringSet("locations", mutableSetOf())
        locations!!.add(search)
        preferences.edit().putStringSet("locations", locations).apply()

        val cityDataByLatLong = locationApi.getLatLongByCity(search)
        return weatherApi.getAllWeatherData(
            cityDataByLatLong.results[0].geometry.lat.toString(),
            cityDataByLatLong.results[0].geometry.lng.toString()
        )
    }
}