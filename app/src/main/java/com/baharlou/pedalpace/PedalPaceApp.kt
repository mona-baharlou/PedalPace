package com.baharlou.pedalpace

import android.app.Application
import com.baharlou.pedalpace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PedalPaceApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PedalPaceApp)
            modules(appModule)
        }
    }
}