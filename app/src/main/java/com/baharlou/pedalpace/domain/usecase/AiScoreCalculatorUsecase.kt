package com.baharlou.pedalpace.domain.usecase

import com.baharlou.pedalpace.domain.model.DailyForecast
import com.baharlou.pedalpace.domain.model.Recommendation
import com.baharlou.pedalpace.domain.model.Score
import com.baharlou.pedalpace.domain.model.WeatherMetric

class AiScoreCalculatorUseCase {

    operator fun invoke(forecast: DailyForecast): Score {
        val tempScore = calculateTempScore(forecast.temperature.day)
        val windScore = calculateWindScore(forecast.windSpeed)
        val rainScore = calculateRainScore(forecast.precipitationProbability)

        // Weighted Average: Temp (40%), Rain (40%), Wind (20%)
        val totalScore = ((tempScore * 0.4) + (rainScore * 0.4) + (windScore * 0.2)).toInt()

        val recommendation = when {
            totalScore >= 85 -> Recommendation.EXCELLENT
            totalScore >= 65 -> Recommendation.GOOD
            totalScore >= 40 -> Recommendation.MODERATE
            else -> Recommendation.POOR
        }

        return Score(
            score = totalScore,
            recommendation = recommendation,
            overallRating = "",
            metrics = listOf(),
            wmetrics = listOf(
                WeatherMetric("Temperature", "${forecast.temperature.day}°C", tempScore),
                WeatherMetric("Wind", "${forecast.windSpeed}m/s", windScore),
                WeatherMetric("Rain Chance", "${(forecast.precipitationProbability * 100).toInt()}%", rainScore)
            ),
            aiReasoning = ""// generateAnalysis(forecast, totalScore)
        )
    }

    private fun calculateTempScore(temp: Int): Int = when (temp) {
        in 18..26 -> 100
        in 10..17, in 27..32 -> 70
        in 0..9, in 33..38 -> 40
        else -> 10
    }

    private fun calculateWindScore(speed: Double): Int = when {
        speed < 3.0 -> 100 // Calm
        speed < 6.0 -> 80  // Light breeze
        speed < 10.0 -> 40 // Challenging
        else -> 0          // Dangerous for cycling
    }

    private fun calculateRainScore(prob: Double): Int = (100 - (prob * 100)).toInt()

    private fun generateAnalysis(f: DailyForecast, score: Int): String {
        return "The cycling conditions are rated $score/100. " +
                "With a temp of ${f.temperature.day}°C and wind at ${f.windSpeed}m/s, " +
                "the primary challenge today is ${if (f.windSpeed > 7) "headwinds" else "nothing significant"}."
    }
}