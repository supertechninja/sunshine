package com.mcwilliams.sunshine.model.allweatherdata

data class Daily(
    val clouds: Int,
    val dew_point: Double,
    val dt: Long,
    val feels_like: FeelsLike,
    val humidity: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Double,
    val sunrise: Int,
    val sunset: Int,
    val temp: Temp,
    val uvi: Double,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_speed: Double
)