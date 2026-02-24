package pusan.university.plato_calendar.domain.entity

import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode

data class AppSettings(
    val notificationsEnabled: Boolean = false,
    val firstReminderTime: NotificationTime = NotificationTime.ONE_HOUR,
    val secondReminderTime: NotificationTime = NotificationTime.ONE_DAY,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
