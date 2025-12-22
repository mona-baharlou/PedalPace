package com.baharlou.pedalpace.di

import com.baharlou.pedalpace.data.remote.WeatherApi
import com.baharlou.pedalpace.data.remote.Config.BASE_URL
import com.baharlou.pedalpace.data.repository.WeatherRepositoryImpl
import com.baharlou.pedalpace.domain.ai.WeatherAiService
import com.baharlou.pedalpace.domain.repository.WeatherRepository
import com.baharlou.pedalpace.domain.usecase.ScoreCalculatorUseCase
import com.baharlou.pedalpace.domain.usecase.FetchForecastUseCase
import com.baharlou.pedalpace.presentation.viewModel.WeatherViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(WeatherApi::class.java)
    }

    //Repository
    single<WeatherRepository> {
        WeatherRepositoryImpl(get())
    }
    // UseCase
    single {
        FetchForecastUseCase(get())
    }

    single { ScoreCalculatorUseCase() }

    single { WeatherAiService(androidContext()) }

    viewModel { WeatherViewModel(get(),
        get(),
        get(),
        get()) }

}