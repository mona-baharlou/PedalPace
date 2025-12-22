package com.baharlou.pedalpace.data.remote

import com.baharlou.pedalpace.domain.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
/**
 * Constraint:
 * This interface MUST use OpenWeather FREE TIER endpoints only.
 * Paid endpoints (One Call 3.0, minute forecast, alerts) are out of scope.
 */
/***
 * ### API Constraint
 * This app uses the OpenWeather Free Tier API exclusively.
 * Data availability, refresh rate, and accuracy are subject to free-tier limitations.
 */
interface WeatherApi {
    @GET("forecast")
    suspend fun getWeather(
        @Query("lat")lat: Double,
        @Query("lon")lon: Double,
        @Query("appid")apiKey: String,
        @Query("units")units: String = "metric"
    ): WeatherResponse
}