package com.baharlou.pedalpace.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.baharlou.pedalpace.domain.model.Recommendation
import com.baharlou.pedalpace.domain.model.Score
import com.baharlou.pedalpace.domain.model.City
import com.baharlou.pedalpace.domain.model.Coordinates
import com.baharlou.pedalpace.domain.model.DailyForecast
import com.baharlou.pedalpace.domain.model.Temperature
import com.baharlou.pedalpace.domain.model.WeatherResponse
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
        if (isGranted) viewModel.checkLocationPermission()
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.checkLocationPermission()
        }
    }

    WeatherScreenContent(
        modifier = modifier,
        weatherState = weatherState,
        dailyScores = dailyScores,
        onFormatDate = { timestamp -> viewModel.formatDate(timestamp) },
        onGetIconUrl = { iconCode -> viewModel.getWeatherIconUrl(iconCode) },
        onRetry = { viewModel.checkLocationPermission() }
    )
}

@Composable
fun WeatherScreenContent(
    weatherState: WeatherState,
    dailyScores: List<Pair<DailyForecast, Score>>,
    onFormatDate: (Long) -> String,
    onGetIconUrl: (String) -> String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF334155)
                    )
                )
            )
    ) {
        val data = weatherState.weatherData
        when {
            weatherState.isLoading && data == null -> LoadingScreen()
            weatherState.error != null -> ErrorScreen(error = weatherState.error, onRetry = onRetry)
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

@Composable
fun WeatherContent(
    weatherData: WeatherResponse,
    dailyScores: List<Pair<DailyForecast, Score>>,
    onFormatDate: (Long) -> String,
    onGetIconUrl: (String) -> String
) {
    val bestDay = remember(dailyScores) { dailyScores.maxByOrNull { it.second.score } }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherHeader(weatherData, bestDay?.first, bestDay?.second,
            formattedBestDate = bestDay?.first?.let { onFormatDate(it.date) } ?: ""
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(dailyScores, key = { it.first.date }) { (forecast, score) ->
                ForecastCard(
                    forecast = forecast,
                    score = score,
                    formattedDate = onFormatDate(forecast.date),
                    weatherIconUrl = onGetIconUrl(forecast.weather.firstOrNull()?.icon ?: ""),
                    isBest = bestDay?.first?.date == forecast.date
                )
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewWeatherScreenContent() {
    val sampleDailyForecast = DailyForecast(
        date = 1734710400L,
        temperature = Temperature(22, 15.0, 25.0, 18.0),
        weather = emptyList(),
        humidity = 40,
        windSpeed = 10.0,
        precipitationProbability = 0.0
    )
    val score = Score(
        score = 85,
        recommendation = Recommendation.EXCELLENT,
        overallRating = "Perfect weather for cycling!",
        metrics = emptyList()
    )

    val sampleWeatherState = WeatherState(
        weatherData = WeatherResponse(
            list = emptyList(),
            city = City(
                id = 1,
                name = "Tehran",
                country = "Iran",
                coord = Coordinates(12.0, 51.255)
            ),
            daily = listOf(sampleDailyForecast)
        ),
        isLoading = false,
        error = null
    )

    PedalPaceTheme {
        WeatherScreenContent(
            weatherState = sampleWeatherState,
            dailyScores = listOf(sampleDailyForecast to score),
            onFormatDate = { "Saturday, Dec 20" },
            onGetIconUrl = { "" },
            onRetry = {}
        )
    }
}