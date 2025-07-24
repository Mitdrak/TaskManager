package com.example.taskmanager

import android.app.Application
import android.util.Log
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
            Timber.plant(ReleaseTree())
            Timber.i("Build de RELEASE, Timber no plantará DebugTree") // Mensaje informativo

        }


    }

}

class ReleaseTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Only log errors and warnings in production
        if (priority == Log.ERROR || priority == Log.WARN) { // Adjust priorities as needed
            // If you have a crash reporting tool (like Crashlytics, Sentry, Bugsnag),
            // you would send errors/warnings there.
            // For KMP, this might involve platform-specific SDK calls.

            // Example: Log to a remote crash reporting service
            // Crashlytics.log("$tag: $message")
            // if (t != null) Crashlytics.recordException(t)

            // You generally *don't* want to print to Logcat in production for these.
            // If you MUST log, ensure it's truly essential and redacted.
        }
        // For debug, info, verbose, and assert levels, do nothing in production.
    }
}
