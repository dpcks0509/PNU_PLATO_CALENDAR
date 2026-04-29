package pusan.university.plato_calendar.presentation.util.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import pusan.university.plato_calendar.data.local.database.AcademicScheduleAlarmDataStore
import pusan.university.plato_calendar.domain.entity.AcademicScheduleAlarmInfo
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.setting.model.AcademicNotificationHour
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime.Companion.getReminderTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val academicScheduleAlarmDataStore: AcademicScheduleAlarmDataStore,
    ) {
        private val alarmManager: AlarmManager by lazy { context.getSystemService(AlarmManager::class.java) }

        fun scheduleNotificationsForSchedule(
            schedule: PersonalScheduleUiModel,
            firstReminderTime: NotificationTime,
            secondReminderTime: NotificationTime,
        ) {
            cancelNotification(schedule.id)

            val now = LocalDateTime.now()

            scheduleReminderIfNeeded(
                schedule = schedule,
                now = now,
                reminderTime = firstReminderTime,
                reminderIndex = 1,
            )

            scheduleReminderIfNeeded(
                schedule = schedule,
                now = now,
                reminderTime = secondReminderTime,
                reminderIndex = 2,
            )
        }

        suspend fun scheduleAcademicNotification(
            title: String,
            startAt: LocalDate,
            endAt: LocalDate,
            startDateHour: AcademicNotificationHour,
            endDateHour: AcademicNotificationHour,
        ) {
            val key = AcademicScheduleAlarmInfo.generateKey(title, startAt, endAt)
            cancelAcademicNotification(key)

            val baseId = academicScheduleAlarmDataStore.getOrCreateNotificationBaseId(key)
            val now = LocalDateTime.now()

            if (startDateHour != AcademicNotificationHour.NONE) {
                val reminderDateTime = LocalDateTime.of(startAt, LocalTime.of(startDateHour.hour, 0))
                if (reminderDateTime.isAfter(now)) {
                    val notificationId = -(baseId * 10 + 1)
                    scheduleNotificationWithScheduleId(
                        notificationId = notificationId,
                        scheduleId = notificationId.toLong(),
                        title = title,
                        message = "• 시작일",
                        calendar = localDateTimeToCalendar(reminderDateTime),
                    )
                }
            }

            if (endDateHour != AcademicNotificationHour.NONE) {
                val reminderDateTime = LocalDateTime.of(endAt, LocalTime.of(endDateHour.hour, 0))
                if (reminderDateTime.isAfter(now)) {
                    val notificationId = -(baseId * 10 + 2)
                    scheduleNotificationWithScheduleId(
                        notificationId = notificationId,
                        scheduleId = notificationId.toLong(),
                        title = title,
                        message = "• 종료일",
                        calendar = localDateTimeToCalendar(reminderDateTime),
                    )
                }
            }
        }

        suspend fun cancelAcademicNotification(key: String) {
            val baseId = academicScheduleAlarmDataStore.getNotificationBaseId(key) ?: return
            listOf(1, 2).forEach { reminderIndex ->
                val notificationId = -(baseId * 10 + reminderIndex)
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        notificationId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        fun cancelAllNotifications(personalSchedules: List<PersonalScheduleUiModel>) {
            personalSchedules.forEach { schedule ->
                cancelNotification(schedule.id)
            }
        }

        fun cancelNotification(scheduleId: Long) {
            listOf(1, 2).forEach { reminderIndex ->
                val notificationId = generateNotificationId(scheduleId, reminderIndex)
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        notificationId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        private fun scheduleReminderIfNeeded(
            schedule: PersonalScheduleUiModel,
            now: LocalDateTime,
            reminderTime: NotificationTime,
            reminderIndex: Int,
        ) {
            if (reminderTime != NotificationTime.NONE) {
                val reminderDateTime = calculateReminderTime(schedule.endAt, reminderTime)
                if (reminderDateTime.isAfter(now)) {
                    val notificationId = generateNotificationId(schedule.id, reminderIndex)
                    val title =
                        if (schedule is CourseScheduleUiModel) schedule.titleWithCourseName else schedule.title

                    scheduleNotificationWithScheduleId(
                        notificationId = notificationId,
                        scheduleId = schedule.id,
                        title = title,
                        message =
                            if (!schedule.description.isNullOrBlank()) {
                                "${schedule.description}\n• ${reminderTime.getReminderTime()}"
                            } else {
                                "• ${reminderTime.getReminderTime()}"
                            },
                        calendar = localDateTimeToCalendar(reminderDateTime),
                    )
                }
            }
        }

        private fun scheduleNotificationWithScheduleId(
            notificationId: Int,
            scheduleId: Long,
            title: String,
            message: String,
            calendar: Calendar,
        ) {
            val triggerTime = calendar.timeInMillis

            val intent =
                Intent(context, AlarmReceiver::class.java).apply {
                    putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                    putExtra(EXTRA_TITLE, title)
                    putExtra(EXTRA_MESSAGE, message)
                    putExtra(EXTRA_SCHEDULE_ID, scheduleId)
                }

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent,
            )
        }

        private fun calculateReminderTime(
            endDateTime: LocalDateTime,
            reminderTime: NotificationTime,
        ): LocalDateTime =
            when (reminderTime) {
                NotificationTime.FIVE_MINUTES -> endDateTime.minusMinutes(5)
                NotificationTime.TEN_MINUTES -> endDateTime.minusMinutes(10)
                NotificationTime.FIFTEEN_MINUTES -> endDateTime.minusMinutes(15)
                NotificationTime.THIRTY_MINUTES -> endDateTime.minusMinutes(30)
                NotificationTime.ONE_HOUR -> endDateTime.minusHours(1)
                NotificationTime.TWO_HOURS -> endDateTime.minusHours(2)
                NotificationTime.SIX_HOURS -> endDateTime.minusHours(6)
                NotificationTime.ONE_DAY -> endDateTime.minusDays(1)
                NotificationTime.TWO_DAYS -> endDateTime.minusDays(2)
                NotificationTime.THREE_DAYS -> endDateTime.minusDays(3)
                NotificationTime.ONE_WEEK -> endDateTime.minusWeeks(1)
                else -> endDateTime
            }

        private fun localDateTimeToCalendar(localDateTime: LocalDateTime): Calendar =
            Calendar.getInstance().apply {
                timeInMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }

        private fun generateNotificationId(
            scheduleId: Long,
            reminderIndex: Int,
        ): Int = (scheduleId * 10 + reminderIndex).toInt()

        companion object {
            const val EXTRA_NOTIFICATION_ID = "notification_id"
            const val EXTRA_TITLE = "title"
            const val EXTRA_MESSAGE = "message"
            const val EXTRA_SCHEDULE_ID = "schedule_id"
        }
    }
