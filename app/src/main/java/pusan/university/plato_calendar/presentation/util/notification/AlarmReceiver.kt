package pusan.university.plato_calendar.presentation.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler.Companion.EXTRA_MESSAGE
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler.Companion.EXTRA_NOTIFICATION_ID
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler.Companion.EXTRA_SCHEDULE_ID
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler.Companion.EXTRA_TITLE
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "PLATO 캘린더"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "예정된 일정이 있습니다."
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L)

        if (notificationId == -1 || scheduleId == -1L) {
            return
        }

        notificationHelper.showNotification(
            notificationId = notificationId,
            scheduleId = scheduleId,
            title = title,
            message = message,
        )
    }
}
