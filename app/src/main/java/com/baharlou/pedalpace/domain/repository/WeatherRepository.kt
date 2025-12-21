package com.baharlou.pedalpace.domain.repository

import com.baharlou.pedalpace.domain.model.WeatherResponse

interface WeatherRepository {
    suspend fun fetchForecast(lat: Double, lon: Double): Result<WeatherResponse>
}