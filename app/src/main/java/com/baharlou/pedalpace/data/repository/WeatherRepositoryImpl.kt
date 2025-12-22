package com.baharlou.pedalpace.data.repository

import com.baharlou.pedalpace.data.remote.Config
import com.baharlou.pedalpace.data.remote.WeatherApi
import com.baharlou.pedalpace.domain.model.WeatherResponse
import com.baharlou.pedalpace.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val apiService: WeatherApi
) : WeatherRepository {
    override suspend fun fetchForecast(
        lat: Double,
        lon: Double
    ): Result<WeatherResponse> {
        return try {
            val response = apiService.getWeather(
                lat = lat,
                lon = lon,
                apiKey = Config.API_KEY
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}