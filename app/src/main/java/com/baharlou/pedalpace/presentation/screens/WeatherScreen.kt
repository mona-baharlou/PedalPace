package com.baharlou.pedalpace.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baharlou.pedalpace.domain.model.*
import com.baharlou.pedalpace.presentation.components.ForecastCard
import com.baharlou.pedalpace.presentation.viewModel.WeatherState
import com.baharlou.pedalpace.presentation.viewModel.WeatherViewModel
import com.baharlou.pedalpace.ui.theme.PedalPaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = koinViewModel()
) {
    val weatherState by viewModel.weatherState
    val dailyScores by viewModel.dailyScores

    WeatherScreenContent(
        modifier = modifier,
        weatherState = weatherState,
        dailyScores = dailyScores,
        onFormatDate = { timestamp -> viewModel.formatDate(timestamp) },
        onGetIconUrl = { iconCode -> viewModel.getWeatherIconUrl(iconCode) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreenContent(
    weatherState: WeatherState,
    dailyScores: List<Pair<DailyForecast, Score>>,
    onFormatDate: (Long) -> String,
    onGetIconUrl: (String) -> String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Weather Forecast",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1E293B)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = weatherState.weatherData?.city?.name ?: "Detecting...",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF1E293B))
                    }
                },
                actions = {
                    // Empty IconButton for symmetry
                    IconButton(onClick = {}, enabled = false) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Transparent)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Text("🌙", fontSize = 20.sp)
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            val data = weatherState.weatherData
            when {
                weatherState.isLoading && data == null -> LoadingScreen()
                weatherState.error != null -> ErrorScreen(error = weatherState.error, onRetry = {})
                data != null -> {
                    WeatherContent(
                        weatherData = data,
                        dailyScores = dailyScores,
                        onFormatDate = onFormatDate,
                        onGetIconUrl = onGetIconUrl
                    )
                }
                else -> WelcomeScreen()
            }
        }
    }
}

@Composable
fun WeatherContent(
    weatherData: WeatherResponse,
    dailyScores: List<Pair<DailyForecast, Score>>,
    onFormatDate: (Long) -> String,
    onGetIconUrl: (String) -> String
) {
    val bestDay = remember(dailyScores) { dailyScores.maxByOrNull { it.second.score } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Excellent Card
        item {
            WeatherHeader(
                weatherData = weatherData,
                score = bestDay?.second,
                formattedDate = bestDay?.first?.let { onFormatDate(it.date) } ?: "Today"
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEXT DAYS",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = {}) {
                    Text("See 7 Days", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                }
            }
        }

        // List of other days
        items(dailyScores.filter { it.first.date != bestDay?.first?.date }) { (forecast, score) ->
            ForecastCard(
                forecast = forecast,
                score = score,
                formattedDate = onFormatDate(forecast.date)
            )
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreenFull() {
    val mockForecasts = listOf(
        DailyForecast(1734710400L, Temperature(25, 15.0, 25.0, 18.0), emptyList(), 40, 12.0, 0.0),
        DailyForecast(1734796800L, Temperature(22, 14.0, 22.0, 16.0), emptyList(), 50, 10.0, 0.1),
        DailyForecast(1734883200L, Temperature(18, 12.0, 18.0, 13.0), emptyList(), 80, 15.0, 0.6)
    )

    val mockScores = listOf(
        Score(85, Recommendation.EXCELLENT, "Perfect weather for cycling!", emptyList()),
        Score(65, Recommendation.GOOD, "A bit cloudy but safe.", emptyList()),
        Score(35, Recommendation.POOR, "High chance of rain.", emptyList())
    )

    val mockState = WeatherState(
        weatherData = WeatherResponse(
            list = emptyList(),
            city = City(1, "Tehran", "IR", Coordinates(35.6, 51.3)),
            daily = mockForecasts
        ),
        isLoading = false
    )

    val dailyScores = mockForecasts.zip(mockScores)

    PedalPaceTheme {
        WeatherScreenContent(
            weatherState = mockState,
            dailyScores = dailyScores,
            onFormatDate = { "Sat, Dec 20" },
            onGetIconUrl = { "" }
        )
    }
}