package com.baharlou.pedalpace.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
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
    val locationPermissionGranted by viewModel.locationPermissionGranted
    val dailyScores by viewModel.dailyScores

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.checkLocationPermission()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkLocationPermission()
        if (!locationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

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
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = weatherState.weatherData?.city?.name ?: "Detecting...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}, enabled = false) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Transparent)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            val data = weatherState.weatherData
            when {
                weatherState.isLoading || (data == null && weatherState.error == null) -> {
                    LoadingScreen()
                }
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
        item {
            WeatherHeader(
                weatherData = weatherData,
                score = bestDay?.second,
                formattedDate = bestDay?.first?.let { onFormatDate(it.date) } ?: "Today"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEXT DAYS",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = {}) {
                    Text("See 5 Days", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(dailyScores.filter { it.first.date != bestDay?.first?.date }) { (forecast, score) ->
            ForecastCard(
                forecast = forecast,
                score = score,
                formattedDate = onFormatDate(forecast.date)
            )
        }
    }
}

// PREVIEWS ///////////////////

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun PreviewWeatherScreenFull() {
    val mockForecasts = listOf(
        DailyForecast(1734710400L, Temperature(25, 15.0, 25.0, 18.0), emptyList(), 40, 12.0, 0.0),
        DailyForecast(1734796800L, Temperature(22, 14.0, 22.0, 16.0), emptyList(), 50, 10.0, 0.1),
        DailyForecast(1734883200L, Temperature(18, 12.0, 18.0, 13.0), emptyList(), 80, 15.0, 0.6)
    )

    val mockScores = listOf(
        Score(85, Recommendation.EXCELLENT, emptyList(),"Perfect weather for cycling!"),
        Score(65, Recommendation.GOOD,emptyList(), "A bit cloudy but safe."),
        Score(35, Recommendation.POOR, emptyList(),"High chance of rain.")
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