package com.example.taskmanager

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.WindowMetricsCalculator
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.taskmanager.data.local.workers.TaskReminderWorker
import com.example.taskmanager.domain.usecase.task.getTaskByIdUseCase
import com.example.taskmanager.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class HabitTrackerApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: CustomWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

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
fun compactScreen() : Boolean {
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(
        context = ContextThemeWrapper(Application(), R.style.Theme_TaskManager)
    )
    val width = metrics.bounds.width()
    val height = metrics.bounds.height()
    val density = Application().resources.displayMetrics.density
    val windowSizeClass = WindowSizeClass.compute(width/density, height/density)

    return windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
            windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
}
class CustomWorkerFactory @Inject constructor(
    private val getTaskByIdUseCase: getTaskByIdUseCase,
    private val notifactionHelper: NotificationHelper
): WorkerFactory(){
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker =
        TaskReminderWorker(
            appContext,
            workerParameters,
            getTaskByIdUseCase,
            notifactionHelper
        )
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
