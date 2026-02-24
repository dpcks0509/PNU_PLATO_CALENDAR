package pusan.university.plato_calendar.presentation.util.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.presentation.main.MainActivity
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler.Companion.EXTRA_NOTIFICATION_ID
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler.Companion.EXTRA_SCHEDULE_ID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper
    @Inject
    constructor(
        @ApplicationContext val context: Context,
    ) {
        private val notificationManager: NotificationManager by lazy {
            context.getSystemService(
                NotificationManager::class.java,
            )
        }

        fun showNotification(
            notificationId: Int,
            scheduleId: Long,
            title: String,
            message: String,
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }

            val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra(EXTRA_SCHEDULE_ID, scheduleId)
                    putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                }

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

            val notification =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(ContextCompat.getColor(context, R.color.primary_color))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setStyle(
                        NotificationCompat
                            .BigTextStyle()
                            .bigText(message),
                    ).build()

            notificationManager.notify(notificationId, notification)
        }

        fun cancelNotification(notificationId: Int) {
            notificationManager.cancel(notificationId)
        }

        companion object {
            const val CHANNEL_ID = "plato_calendar_notification"
            const val CHANNEL_NAME = "PLATO 캘린더 일정"
        }
    }
