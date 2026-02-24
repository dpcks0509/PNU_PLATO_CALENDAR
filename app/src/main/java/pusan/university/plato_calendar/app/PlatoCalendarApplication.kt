package pusan.university.plato_calendar.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pusan.university.plato_calendar.BuildConfig
import pusan.university.plato_calendar.presentation.util.manager.NotificationSyncManager
import pusan.university.plato_calendar.presentation.util.notification.NotificationHelper.Companion.CHANNEL_ID
import pusan.university.plato_calendar.presentation.util.notification.NotificationHelper.Companion.CHANNEL_NAME
import javax.inject.Inject

@HiltAndroidApp
class PlatoCalendarApplication : Application() {
    @Inject
    lateinit var notificationSyncManager: NotificationSyncManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        createNotificationChannel(applicationContext)
        notificationSyncManager.startSync(applicationScope)
    }

    private fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(
                NotificationManager::class.java,
            )

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

        notificationManager.createNotificationChannel(channel)
    }
}
