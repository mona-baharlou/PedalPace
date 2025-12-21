package com.baharlou.pedalpace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baharlou.pedalpace.domain.model.DailyForecast
import com.baharlou.pedalpace.domain.model.Recommendation
import com.baharlou.pedalpace.domain.model.Score
import com.baharlou.pedalpace.domain.model.Temperature
import com.baharlou.pedalpace.ui.theme.BikeOrange
import com.baharlou.pedalpace.ui.theme.BikeOrangeLight
import com.baharlou.pedalpace.ui.theme.PedalPaceTheme

@Composable
fun ForecastCard(
    forecast: DailyForecast,
    score: Score,
    formattedDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BikeOrangeLight,
                modifier = Modifier.size(48.dp)
            ) {
                //icon Box
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (score.score < 40) "🌧️" else "☁️", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedDate,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${score.score}%",
                        color = BikeOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = score.overallRating.take(30) + "...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${forecast.temperature.max.toInt()}° / ${forecast.temperature.min.toInt()}°",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
private fun PreviewForecastCardExcellent() {
    val mockForecast = DailyForecast(
        date = 1734710400L,
        temperature = Temperature(day = 28, min = 16.0, max = 28.0, night = 18.0),
        weather = emptyList(),
        humidity = 40,
        windSpeed = 12.0,
        precipitationProbability = 0.0
    )

    val mockScore = Score(
        score = 92,
        recommendation = Recommendation.EXCELLENT,
        overallRating = "Perfect visibility and light winds.",
        metrics = emptyList()
    )

    PedalPaceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ForecastCard(
                forecast = mockForecast,
                score = mockScore,
                formattedDate = "Sunday, Dec 21"
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
private fun PreviewForecastCardPoor() {
    val mockForecast = DailyForecast(
        date = 1734796800L,
        temperature = Temperature(day = 14, min = 10.0, max = 14.0, night = 11.0),
        weather = emptyList(),
        humidity = 90,
        windSpeed = 25.0,
        precipitationProbability = 0.8
    )

    val mockScore = Score(
        score = 25,
        recommendation = Recommendation.POOR,
        overallRating = "Heavy rain expected all day.",
        metrics = emptyList()
    )

    PedalPaceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ForecastCard(
                forecast = mockForecast,
                score = mockScore,
                formattedDate = "Monday, Dec 22"
            )
        }
    }
}