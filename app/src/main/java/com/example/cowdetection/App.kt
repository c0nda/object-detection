package com.example.cowdetection

import android.app.Application
import com.example.cowdetection.di.AppComponent
import com.example.cowdetection.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .appContext(this)
            .build()
    }
}