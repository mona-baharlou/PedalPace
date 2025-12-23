package com.baharlou.pedalpace.domain.model

data class Score(
    val score: Int, //0-100
    val recommendation: Recommendation,
    val metrics: List<Metric>,
    val wmetrics: List<WeatherMetric>,
    val overallRating: String,
    val aiReasoning: String? = null
)

enum class Recommendation {
    EXCELLENT,
    GOOD,
    MODERATE,
    POOR,
    DANGEROUS
}

data class Metric(
    val name: String,
    val score: Int,
    val weight: Double,
    val description: String,
    val icon: String
)

data class WeatherMetric(
    val name: String,
    val value: String,
    val score: Int // Individual score for that metric
)