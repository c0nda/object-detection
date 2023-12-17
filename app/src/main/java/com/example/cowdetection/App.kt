package com.example.cowdetection

import android.app.Application
import com.example.cowdetection.di.DaggerAppComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        DI.appComponent = DaggerAppComponent
            .builder()
            .appContext(this)
            .build()
    }
}