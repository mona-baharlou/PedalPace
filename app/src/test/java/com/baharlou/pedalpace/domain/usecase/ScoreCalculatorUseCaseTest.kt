package com.baharlou.pedalpace.domain.usecase

import com.baharlou.pedalpace.domain.model.*
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ScoreCalculatorUseCaseTest {

    private val calculator = ScoreCalculatorUseCase()

    @Test
    fun `when weather is perfect, score should be high`() {
        // Arrange: 22°C, no wind, no rain, clear sky
        val perfectForecast = DailyForecast(
            date = 123456789L,
            temperature = Temperature(day = 22, min = 18.0, max = 22.0, night = 20.0),
            weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
            humidity = 45,
            windSpeed = 2.0, // ~7 km/h
            precipitationProbability = 0.0
        )

        val result = calculator(perfectForecast)

        // Assert
        assertThat(result.score).isAtLeast(90)
        assertThat(result.recommendation).isEqualTo(Recommendation.EXCELLENT)
    }

    @Test
    fun `when wind is extreme, score should be poor`() {
        // Arrange: 50 km/h wind (approx 14 m/s)
        val windyForecast = DailyForecast(
            date = 123456789L,
            temperature = Temperature(day = 20, min = 15.0, max = 20.0, night = 18.0),
            weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
            humidity = 50,
            windSpeed = 14.0,
            precipitationProbability = 0.0
        )

        val result = calculator(windyForecast)

        // Assert
        // Wind has a 0.20 weight, so high wind should significantly drop the score
        assertThat(result.score).isAtMost(70)
        val windMetric = result.metrics.find { it.name == "Wind" }
        assertThat(windMetric?.score).isEqualTo(0)
    }

    @Test
    fun `when it is freezing, recommendation should be poor or dangerous`() {
        // Arrange: -15°C
        val freezingForecast = DailyForecast(
            date = 123456789L,
            temperature = Temperature(day = -15, min = -20.0, max = -15.0, night = -18.0),
            weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
            humidity = 30,
            windSpeed = 2.0,
            precipitationProbability = 0.0
        )

        val result = calculator(freezingForecast)

        // Assert
        assertThat(result.recommendation).isAnyOf(Recommendation.POOR, Recommendation.DANGEROUS)
    }
}