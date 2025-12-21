package com.baharlou.pedalpace.domain.model

data class Score(
    val score: Int,
    val recommendation: Recommendation,
    val metrics: List<Metric>,
    val overallRating: String
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