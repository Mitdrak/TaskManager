package com.example.taskmanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class HabitTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber inicializado para builds de DEBUG") // Mensaje para confirmar

        } else {
            Timber.i("Build de RELEASE, Timber no plantará DebugTree") // Mensaje informativo
        }


    }

}