package com.baharlou.pedalpace.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baharlou.pedalpace.domain.model.Recommendation
import com.baharlou.pedalpace.domain.model.Score
import com.baharlou.pedalpace.domain.model.City
import com.baharlou.pedalpace.domain.model.Coordinates
import com.baharlou.pedalpace.domain.model.DailyForecast
import com.baharlou.pedalpace.domain.model.Temperature
import com.baharlou.pedalpace.domain.model.WeatherResponse
import com.baharlou.pedalpace.ui.theme.PedalPaceTheme

@Composable
fun WeatherHeader(
    weatherData: WeatherResponse,
    bestForecast: DailyForecast?,
    bestScore: Score?,
    formattedBestDate: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "️ Weather Forecast",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            weatherData.city?.let { city ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${city.name}, ${city.country}",
                    color = Color(0xFFCBD5E1),
                    fontSize = 16.sp
                )
            }

            if (bestForecast != null && bestScore != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🏆 Best day: ",
                        color = Color(0xFF22C55E),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formattedBestDate,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bestScore.overallRating,
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            }
        }
    }
}

// --- PREVIEW ---

@Preview
@Composable
private fun PreviewWeatherHeader() {
    PedalPaceTheme {
        WeatherHeader(
            weatherData = WeatherResponse(
                list = emptyList(),
                city =City(
                    id = 1,
                    name = "Tehran",
                    country = "Iran",
                    coord = Coordinates(12.0, 51.255)
                ),
                daily = emptyList()
            ),
            bestForecast = DailyForecast(
                date = 1734710400L,
                temperature = Temperature(20, 10.0, 25.0, 15.0),
                weather = emptyList(),
                humidity = 50,
                windSpeed = 5.0,
                precipitationProbability = 0.0
            ),
            bestScore = Score(
                score = 95,
                recommendation = Recommendation.EXCELLENT,
                overallRating = "Perfect light winds and sunshine!",
                metrics = emptyList()
            ),
            formattedBestDate = "Monday, Dec 22"
        )
    }
}