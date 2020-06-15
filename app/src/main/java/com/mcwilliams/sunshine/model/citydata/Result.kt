package com.mcwilliams.sunshine.model.citydata

data class Result(
    val bounds: Bounds,
    val confidence: Int,
    val formatted: String,
    val geometry: Geometry
)