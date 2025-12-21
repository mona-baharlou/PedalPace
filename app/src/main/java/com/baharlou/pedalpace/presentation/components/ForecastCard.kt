package com.baharlou.pedalpace.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.baharlou.pedalpace.domain.model.Recommendation
import com.baharlou.pedalpace.domain.model.Score
import com.baharlou.pedalpace.domain.model.DailyForecast
import com.baharlou.pedalpace.domain.model.Temperature
import com.baharlou.pedalpace.ui.theme.PedalPaceTheme


@Composable
fun ForecastCard(
    forecast: DailyForecast,
    score: Score,
    formattedDate: String,
    weatherIconUrl: String,
    isBest: Boolean,
    modifier: Modifier = Modifier
) {
    val scoreColor = getScoreColor(score.score)
    val backgroundColor = if (isBest) {
        Color(0xFF064E3B).copy(alpha = 0.3f)
    } else {
        Color(0xFF1E293B).copy(alpha = 0.8f) // Standard dark blue-gray
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isBest) Modifier.border(
                    3.dp,
                    Color(0xFF22C55E),
                    RoundedCornerShape(20.dp)
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Date and Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formattedDate,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = score.recommendation.name,
                        color = scoreColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                CircularProgressBar(
                    score = score.score,
                    color = scoreColor,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = score.overallRating,
                color = Color(0xFFCBD5E1),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weather Info Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = weatherIconUrl,
                    contentDescription = forecast.weather.firstOrNull()?.description,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "${forecast.temperature.max.toInt()}° / ${forecast.temperature.min.toInt()}°",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = forecast.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Factors Horizontal List
            val maxFactorHeight = remember(score.metrics) {
                val baseHeight = 100.dp
                if (score.metrics.any { it.description.length > 25 }) 130.dp else baseHeight
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 4.dp)
            ) {
                items(score.metrics) { factor ->
                    FactorItem(
                        factor = factor,
                        height = maxFactorHeight
                    )
                }
            }
        }
    }
}

/**
 * Helper to determine score color
 */
fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF22C55E) // Green
        score >= 60 -> Color(0xFF4ADE80) // Light Green
        score >= 40 -> Color(0xFFFACC15) // Yellow
        score >= 20 -> Color(0xFFF87171) // Light Red
        else -> Color(0xFFDC2626) // Red
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun PreviewForecastCard() {
    val mockForecast = DailyForecast(
        date = 1734710400L,
        temperature = Temperature(day = 24, min = 16.0, max = 28.0, night = 18.0),
        weather = emptyList(),
        humidity = 40,
        windSpeed = 12.0,
        precipitationProbability = 0.0
    )

    val mockScore = Score(
        score = 85,
        recommendation = Recommendation.EXCELLENT,
        overallRating = "The wind is low and the temperature is perfect for a long ride.",
        metrics = emptyList()
    )

    PedalPaceTheme {
        Box(Modifier.padding(16.dp)) {
            ForecastCard(
                forecast = mockForecast,
                score = mockScore,
                formattedDate = "Saturday, Dec 20",
                weatherIconUrl = "https://openweathermap.org/img/wn/01d@2x.png",
                isBest = true
            )
        }
    }
}