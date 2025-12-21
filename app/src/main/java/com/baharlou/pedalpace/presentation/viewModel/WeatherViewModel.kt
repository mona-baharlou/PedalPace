package com.baharlou.pedalpace.presentation.viewModel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baharlou.pedalpace.data.remote.Config
import com.baharlou.pedalpace.domain.model.Score
import com.baharlou.pedalpace.domain.model.DailyForecast
import com.baharlou.pedalpace.domain.model.Temperature
import com.baharlou.pedalpace.domain.model.WeatherResponse
import com.baharlou.pedalpace.domain.usecase.ScoreCalculatorUseCase
import com.baharlou.pedalpace.domain.usecase.FetchForecastUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherViewModel(
    application: Application,
    private val getWeatherForecastUseCase: FetchForecastUseCase,
    private val calculateBikeRidingScoreUseCase: ScoreCalculatorUseCase
) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    // Permission State
    private val _locationPermissionGranted = mutableStateOf(false)
    val locationPermissionGranted: State<Boolean> = _locationPermissionGranted

    // Main Weather State
    private val _weatherState = mutableStateOf(WeatherState())
    val weatherState: State<WeatherState> = _weatherState

    // Processed Scores State
    private val _dailyScores = mutableStateOf<List<Pair<DailyForecast, Score>>>(emptyList())
    val dailyScores: State<List<Pair<DailyForecast, Score>>> = _dailyScores

    init {
        checkLocationPermission()
    }

    fun checkLocationPermission() {
        val context = getApplication<Application>()
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        _locationPermissionGranted.value = hasPermission
        if (hasPermission) {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        fetchWeatherData(it.latitude, it.longitude)
                    } ?: run {
                        _weatherState.value = _weatherState.value.copy(
                            isLoading = false,
                            error = "Location is null. Please ensure GPS is enabled."
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    _weatherState.value = _weatherState.value.copy(
                        isLoading = false,
                        error = "Failed to get location: ${exception.message}"
                    )
                }
        } catch (e: SecurityException) {
            _weatherState.value = _weatherState.value.copy(error = "Permission denied")
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        _weatherState.value = _weatherState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            getWeatherForecastUseCase(latitude, longitude)
                .onSuccess { response ->
                    val dailyForecasts = processForecastIntoDaily(response)
                    val scores = dailyForecasts.map { forecast ->
                        forecast to calculateBikeRidingScoreUseCase(forecast)
                    }

                    _dailyScores.value = scores
                    _weatherState.value = _weatherState.value.copy(
                        isLoading = false,
                        weatherData = response.copy(daily = dailyForecasts),
                        error = null
                    )
                }
                .onFailure { exception ->
                    _weatherState.value = _weatherState.value.copy(
                        isLoading = false,
                        error = "Network Error: ${exception.localizedMessage}"
                    )
                }
        }
    }

    private fun processForecastIntoDaily(response: WeatherResponse): List<DailyForecast> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return response.list
            .groupBy { dateFormat.format(it.date * 1000) }
            .values
            .take(6) // Limit to 6 days
            .map { dayList ->
                val first = dayList.first()
                DailyForecast(
                    date = first.date,
                    temperature = Temperature(
                        day = dayList.map { it.main.temp }.average().toInt(),
                        min = dayList.minOf { it.main.tempMin },
                        max = dayList.maxOf { it.main.tempMax },
                        night = dayList.last().main.temp
                    ),
                    weather = listOf(
                        dayList.flatMap { it.weather }
                            .groupBy { it.main }
                            .maxByOrNull { it.value.size }?.value?.first() ?: first.weather.first()
                    ),
                    humidity = dayList.map { it.main.humidity }.average().toInt(),
                    windSpeed = dayList.map { it.wind.speed }.average(),
                    precipitationProbability = dayList.map { it.precipitationProbability }.average()
                )
            }
    }

    // --- UI Helpers ---

    fun getWeatherIconUrl(iconCode: String): String {
        return if (iconCode.isNotEmpty()) {
            "${Config.ICON_URL}$iconCode@2x.png"
        } else ""
    }

    fun formatDate(timestamp: Long): String {
        return try {
            val date = Date(timestamp * 1000)
            val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
            dateFormat.format(date)
        } catch (e: Exception) {
            "Unknown Date"
        }
    }
}

data class WeatherState(
    val isLoading: Boolean = false,
    val weatherData: WeatherResponse? = null,
    val error: String? = null,
    val locationPermissionGranted: Boolean = false
)