package com.baharlou.pedalpace.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baharlou.pedalpace.domain.model.*
import com.baharlou.pedalpace.presentation.components.CircularProgressBar
import com.baharlou.pedalpace.presentation.components.WeatherType
import com.baharlou.pedalpace.ui.theme.BikeGreen
import com.baharlou.pedalpace.ui.theme.BikeGreenDark
import com.baharlou.pedalpace.ui.theme.BikeGreenLight
import com.baharlou.pedalpace.ui.theme.PedalPaceTheme

@Composable
fun WeatherHeader(
    weatherData: WeatherResponse,
    score: Score?,
    formattedDate: String
) {
    val weatherType = remember(weatherData) {
        val desc = weatherData.daily.firstOrNull()?.weather?.firstOrNull()?.description?.lowercase() ?: ""
        when {
            desc.contains("rain") -> WeatherType.RAIN
            desc.contains("snow") -> WeatherType.SNOW
            desc.contains("cloud") -> WeatherType.CLOUDY
            else -> WeatherType.SUNNY
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
        ) {
           // WeatherEffectBackground(weatherType = weatherType)

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = formattedDate,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = BikeGreenLight,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "✓ BEST DAY FOR CYCLING",
                                color = BikeGreenDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    CircularProgressBar(
                        score = score?.score ?: 0,
                        modifier = Modifier.size(80.dp)
                    )
                }

                // Recommendation Banner
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = score?.recommendation?.name ?: "UNKNOWN",
                                color = BikeGreen, // Accent color stays
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = score?.overallRating ?: "",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                        Text("🚲", fontSize = 28.sp)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentTemp = weatherData.daily.firstOrNull()?.temperature?.day?.toInt() ?: 0
                    val minTemp = weatherData.daily.firstOrNull()?.temperature?.min?.toInt() ?: 0

                    Text(
                        text = "$currentTemp°",
                        fontSize = 68.sp,
                        fontWeight = FontWeight.W200,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "/ $minTemp°",
                        // THEME AWARE: Muted text
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 22.sp,
                        modifier = Modifier.padding(top = 18.dp, start = 4.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.12f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ), label = "iconScale"
                    )

                    Text(
                        text = when (weatherType) {
                            WeatherType.RAIN -> "🌧️"
                            WeatherType.SNOW -> "❄️"
                            WeatherType.CLOUDY -> "☁️"
                            else -> "☀️"
                        },
                        fontSize = 52.sp,
                        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val daily = weatherData.daily.firstOrNull()
                    MetricItem(
                        modifier = Modifier.weight(1f),
                        value = "${daily?.windSpeed?.toInt() ?: 0} km/h",
                        label = "WIND",
                        icon = "🌀"
                    )
                    MetricItem(
                        modifier = Modifier.weight(1f),
                        value = "${(daily?.precipitationProbability?.times(100))?.toInt() ?: 0}%",
                        label = "PRECIP",
                        icon = "💧"
                    )
                    MetricItem(
                        modifier = Modifier.weight(1f),
                        value = "6:30 PM",
                        label = "SUNSET",
                        icon = "🌅"
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItem(
    modifier: Modifier,
    value: String,
    label: String,
    icon: String
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
private fun PreviewWeatherHeaderExcellent() {
    val mockForecast = DailyForecast(
        date = 1734710400L,
        temperature = Temperature(day = 25, min = 15.0, max = 25.0, night = 18.0),
        weather = listOf(Weather(1, "clear sky", "01d", icon = Icons.Default.WbSunny.toString())),
        humidity = 40,
        windSpeed = 12.0,
        precipitationProbability = 0.05
    )

    val mockWeatherResponse = WeatherResponse(
        list = emptyList(),
        city = City(id = 1, name = "Tehran", country = "IR", coord = Coordinates(0.0, 0.0)),
        daily = listOf(mockForecast)
    )

    val mockScore = Score(
        score = 85,
        recommendation = Recommendation.EXCELLENT,
        overallRating = "The wind is low and the temperature is perfect for a long ride.",
        metrics = emptyList(),
        aiReasoning = "",
        wmetrics = emptyList()
    )

    PedalPaceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            WeatherHeader(
                weatherData = mockWeatherResponse,
                score = mockScore,
                formattedDate = "Saturday, Dec 20"
            )
        }
    }
}