package com.baharlou.pedalpace.domain.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.baharlou.pedalpace.BuildConfig
import com.baharlou.pedalpace.R
import com.baharlou.pedalpace.domain.model.WeatherResponse

class GetAiTipUseCase(private val context: android.content.Context) {
    //  the Gemini Flash model
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun getProTip(weather: WeatherResponse): String? {
        val daily = weather.daily.firstOrNull() ?: return null

        val prompt = """
            You are a professional cycling coach. Based on these weather conditions:
            Temp: ${daily.temperature.day}°C, 
            Wind: ${daily.windSpeed} km/h, 
            Precipitation: ${daily.precipitationProbability * 100}%.
            
            Give a 1-sentence "Pro-Tip" for a cyclist today. 
            Focus on gear, safety, or technique. Be encouraging but brief.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text
        } catch (e: Exception) {
            context.getString(R.string.check_your_tire_pressure_and_stay_hydrated)
        }
    }
}

    /*suspend fun getProTip(weatherData: WeatherResponse): String {

        val daily = weatherData.daily.firstOrNull() ?: return ""

        val prompt = "Give a cycling tip for ${daily.temperature}°C and ${daily.windSpeed}."
        return try {
            val response = generativeModel.generateContent(prompt)
            // Log the response to Logcat
            println("GEMINI DEBUG: ${response.text}")
            response.text ?: "AI returned empty text"
        } catch (e: Exception) {
            // This is where your current message is coming from.
            // Let's see the ACTUAL error:
            println("GEMINI ERROR: ${e.message}")
            "Error: ${e.localizedMessage}"
        }
    }*/

