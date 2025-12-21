package com.baharlou.pedalpace.domain.usecase

import com.baharlou.pedalpace.domain.model.WeatherResponse
import com.baharlou.pedalpace.domain.repository.WeatherRepository

class FetchForecastUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<WeatherResponse> {
        return repository.fetchForecast(lat, lon)

    }
}