package com.baharlou.pedalpace.domain.usecase

import com.baharlou.pedalpace.domain.model.*

/**
 * Business logic to determine the "Cycling Suitability Score" based on weather conditions.
 */
class ScoreCalculatorUseCase {

    companion object {
        // Weights (Must sum to 1.0)
        private const val WEIGHT_TEMP = 0.25
        private const val WEIGHT_WIND = 0.20
        private const val WEIGHT_PRECIP = 0.25
        private const val WEIGHT_WEATHER = 0.20
        private const val WEIGHT_HUMIDITY = 0.10

        // Thresholds
        private const val MS_TO_KMH_RATIO = 3.6
        private const val OPTIMAL_TEMP_MIN = 15.0
        private const val OPTIMAL_TEMP_MAX = 25.0
    }

    operator fun invoke(forecast: DailyForecast): Score {
        val metrics = listOf(
            calculateTemperatureMetric(forecast.temperature.max),
            calculateWindMetric(forecast.windSpeed),
            calculatePrecipitationMetric(forecast.precipitationProbability),
            calculateWeatherConditionMetric(forecast.weather.firstOrNull()),
            calculateHumidityMetric(forecast.humidity)
        )

       // val totalScore = metrics.sumOf { it.score * it.weight }.toInt()

        // 1. Calculate the initial weighted score
        var totalScore = metrics.sumOf { it.score * it.weight }.toInt()

        // 2. SAFETY OVERRIDE: If temperature is freezing or wind is extreme,
        // the ride cannot be "GOOD" regardless of other metrics.
        val isFreezing = forecast.temperature.max < -10.0
        val isExtremeWind = (forecast.windSpeed * 3.6) > 35.0
        val isStormy = (forecast.weather.firstOrNull()?.id ?: 800) < 300 // Thunderstorms

        // If any of above conditions is met, we cap the score at 30 (Poor)
        val safetyTriggered = isFreezing || isExtremeWind || isStormy
        if (safetyTriggered) {
            totalScore = totalScore.coerceAtMost(30)
        }


        return Score(
            score = totalScore,
            recommendation = mapScoreToRecommendation(totalScore),
            metrics = metrics,
            overallRating = mapScoreToRatingMessage(totalScore, safetyTriggered, isFreezing, isExtremeWind),
            wmetrics = emptyList(),
            aiReasoning = ""
        )
    }

    // Temperature Logic

    private fun calculateTemperatureMetric(temp: Double) = Metric(
        name = "Temperature",
        weight = WEIGHT_TEMP,
        score = when {
            temp < -10 || temp > 40 -> 0      // Extreme
            temp in 15.0..25.0 -> 100         // Optimal
            temp in 10.0..30.0 -> 80          // Good
            temp in 0.0..35.0 -> 40           // Acceptable
            else -> 10                        // Freezing or very hot
        },
        description = when {
            temp < 0 -> "Very cold, wear thermal gear"
            temp < 12 -> "Chilly, wear layers"
            temp in OPTIMAL_TEMP_MIN..OPTIMAL_TEMP_MAX -> "Perfect cycling temperature"
            temp < 32 -> "Warm, stay hydrated"
            else -> "Extremely hot, stay safe"
        },
        icon = if (temp in OPTIMAL_TEMP_MIN..OPTIMAL_TEMP_MAX) "🌡️" else if (temp > 25) "🔥" else "❄️"
    )

    //  Wind Logic

    private fun calculateWindMetric(windSpeedMs: Double): Metric {
        val kmh = windSpeedMs * MS_TO_KMH_RATIO
        val score = when {
            kmh < 10 -> 100
            kmh < 15 -> 80
            kmh < 22 -> 50
            kmh < 30 -> 20
            else -> 0 //extreme wind
        }
        return Metric(
            name = "Wind",
            weight = WEIGHT_WIND,
            score = score,
            description = when {
                kmh < 10 -> "Calm, perfect for any ride"
                kmh < 20 -> "Moderate breeze"
                else -> "Strong winds, expect resistance"
            },
            icon = if (kmh < 15) "🍃" else "💨"
        )
    }

    //  Precipitation Logic

    private fun calculatePrecipitationMetric(prob: Double) = Metric(
        name = "Precipitation",
        weight = WEIGHT_PRECIP,
        score = (100 * (1.0 - prob)).toInt().coerceIn(0, 100),
        description = when {
            prob < 0.1 -> "Dry conditions expected"
            prob < 0.4 -> "Light rain possible"
            else -> "High rain probability"
        },
        icon = if (prob < 0.2) "☀️" else "🌧️"
    )

    //  Weather Condition (ID-based)

    private fun calculateWeatherConditionMetric(weather: Weather?) = Metric(
        name = "Condition",
        weight = WEIGHT_WEATHER,
        score = when (val id = weather?.id ?: 800) {
            800 -> 100 // Clear
            in 801..804 -> 85 // Clouds
            in 701..781 -> 60 // Atmosphere
            in 300..321 -> 40 // Drizzle
            in 500..531 -> 20 // Rain
            else -> 0 // Storms/Snow
        },
        description = weather?.description?.replaceFirstChar { it.uppercase() } ?: "Clear skies",
        icon = "🌤️"
    )

    //  Humidity Logic

    private fun calculateHumidityMetric(humidity: Int) = Metric(
        name = "Humidity",
        weight = WEIGHT_HUMIDITY,
        score = when (humidity) {
            in 35..55 -> 100
            in 30..70 -> 80
            else -> 50
        },
        description = if (humidity > 70) "Humid air, feels heavier" else "Comfortable air quality",
        icon = "💧"
    )

    //  Final Mapping

    /*private fun mapScoreToRecommendation(score: Int) = when {
        score >= 85 -> Recommendation.EXCELLENT
        score >= 70 -> Recommendation.GOOD
        score >= 50 -> Recommendation.MODERATE
        else -> Recommendation.POOR
    }
*/

    private fun mapScoreToRecommendation(score: Int) = when {
        score >= 85 -> Recommendation.EXCELLENT
        score >= 70 -> Recommendation.GOOD
        score >= 50 -> Recommendation.MODERATE
        score >= 25 -> Recommendation.POOR
        else -> Recommendation.POOR
    }

    private fun mapScoreToRatingMessage(
        score: Int,
        safetyTriggered: Boolean,
        isFreezing: Boolean,
        isWindy: Boolean
    ): String {
        return when {
            isFreezing -> "Dangerous cold! Stay indoors. ❄️"
            isWindy -> "Extreme winds! High risk of crashes. 💨"
            safetyTriggered -> "Hazardous conditions detected. ⚠️"
            score >= 85 -> "Perfect for cycling! "
            score >= 70 -> "Great conditions! 🌤️"
            score >= 50 -> "Manageable conditions ⚠️"
            else -> "Better to stay indoors today 🏠"
        }
    }

   /* private fun mapScoreToRatingMessage(score: Int) = when {
        score >= 85 -> "Perfect for cycling! 🚴"
        score >= 70 -> "Great conditions! 🌤️"
        score >= 50 -> "Manageable conditions ⚠️"
        else -> "Better to stay indoors today 🏠"
    }*/
}