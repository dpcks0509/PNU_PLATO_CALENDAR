package pusan.university.plato_calendar.domain.entity

import pusan.university.plato_calendar.presentation.setting.model.NotificationTime

data class ScheduleAlarmInfo(
    val isCompleted: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val firstReminderTime: NotificationTime = NotificationTime.FIRST_REMINDER_TIME,
    val secondReminderTime: NotificationTime = NotificationTime.SECOND_REMINDER_TIME,
    val isCustomized: Boolean = false,
)
