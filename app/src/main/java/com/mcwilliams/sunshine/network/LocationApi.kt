package com.mcwilliams.sunshine.network

import com.mcwilliams.sunshine.model.citydata.CityDataByLatLong
import retrofit2.http.GET
import retrofit2.http.Query


interface LocationApi {
    @GET("geocode/v1/json")
    suspend fun getLatLongByCity(
        @Query("q") city: String,
        @Query("key") key: String = ""
    ): CityDataByLatLong
}