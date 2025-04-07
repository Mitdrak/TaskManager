package com.example.taskmanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HabitTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }

}