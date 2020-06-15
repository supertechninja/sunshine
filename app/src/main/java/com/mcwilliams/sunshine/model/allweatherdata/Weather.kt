package com.mcwilliams.sunshine.model.allweatherdata

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)