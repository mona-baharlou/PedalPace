package com.baharlou.pedalpace.presentation.screens

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
import com.baharlou.pedalpace.domain.model.City
import com.baharlou.pedalpace.domain.model.Coordinates
import com.baharlou.pedalpace.domain.model.DailyForecast
import com.baharlou.pedalpace.domain.model.Recommendation
import com.baharlou.pedalpace.domain.model.Score
import com.baharlou.pedalpace.domain.model.Temperature
import com.baharlou.pedalpace.domain.model.WeatherResponse
import com.baharlou.pedalpace.presentation.components.CircularProgressBar
import com.baharlou.pedalpace.ui.theme.PedalPaceTheme

@Composable
fun WeatherHeader(
    weatherData: WeatherResponse,
    score: Score?,
    formattedDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(formattedDate, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            "✓ BEST DAY FOR CYCLING",
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
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
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("EXCELLENT", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                        Text(score?.overallRating ?: "", color = Color.Gray, fontSize = 14.sp)
                    }
                    Text("🚲", fontSize = 24.sp)
                }
            }

            // Temperature Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("25°", fontSize = 64.sp, fontWeight = FontWeight.Light)
                Text("/ 15°", color = Color.Gray, fontSize = 20.sp, modifier = Modifier.padding(top = 16.dp))
                Spacer(modifier = Modifier.weight(1f))
                Text("☀️", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Metrics
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricItem(Modifier.weight(1f), "12 km/h", "WIND", "🌀")
                MetricItem(Modifier.weight(1f), "10%", "PRECIP", "💧")
                MetricItem(Modifier.weight(1f), "6:30 PM", "SUNSET", "🌅")
            }
        }
    }
}

@Composable
fun MetricItem(modifier: Modifier, value: String, label: String, icon: String) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 20.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
private fun PreviewWeatherHeader() {
    val mockForecast = DailyForecast(
        date = 1734710400L,
        temperature = Temperature(day = 25, min = 15.0, max = 25.0, night = 18.0),
        weather = emptyList(),
        humidity = 40,
        windSpeed = 12.0,
        precipitationProbability = 0.1
    )

    val mockWeatherResponse = WeatherResponse(
        list = emptyList(),
        city = City(
            id = 1,
            name = "Tehran",
            country = "Iran",
            coord = Coordinates(35.6892, 51.3890)
        ),
        daily = listOf(mockForecast)
    )

    val mockScore = Score(
        score = 85,
        recommendation = Recommendation.EXCELLENT,
        overallRating = "Perfect weather for cycling!",
        metrics = emptyList()
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