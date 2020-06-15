package com.mcwilliams.sunshine.network

import com.mcwilliams.sunshine.model.weatherdata.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

val apiKey = ""

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getLatLongByCity(
        @Query("lat") lat: String,
        @Query("lon") long: String,
        @Query("units") units: String = "Imperial",
        @Query("appid") key: String = apiKey
    ): WeatherData

    @GET("data/2.5/weather")
    suspend fun getWeatherDataByCity(
        @Query("q") city: String,
        @Query("units") units: String = "Imperial",
        @Query("appid") key: String = apiKey
    ): WeatherData

    @GET("data/2.5/weather")
    suspend fun getWeatherDataByZipCode(
        @Query("zip") zipCode: String,
        @Query("units") units: String = "Imperial",
        @Query("appid") key: String = apiKey
    ): WeatherData

    @GET("data/2.5/onecall")
    suspend fun getAllWeatherData(
        @Query("lat") lat: String,
        @Query("lon") long: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = "Imperial",
        @Query("appid") key: String = apiKey
    ): com.mcwilliams.sunshine.model.allweatherdata.WeatherData

}