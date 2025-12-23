package com.baharlou.pedalpace.data.remote

import com.baharlou.pedalpace.BuildConfig

object Config {
    const val API_KEY ="e4ee7a511491b4d4cc201c0510057da5"

    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val ICON_URL = "https://openweathermap.org/img/wn/"

    private const val MIN_REFRESH_INTERVAL_MS = 60 * 60 * 1000L // 1 hour

}