package pusan.university.plato_calendar.presentation.setting.intent

import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import pusan.university.plato_calendar.presentation.util.base.UiState

data class SettingState(
    val userInfo: String? = null,
    val autoUpdateSchedule: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val firstReminderTime: NotificationTime = NotificationTime.FIRST_REMINDER_TIME,
    val secondReminderTime: NotificationTime = NotificationTime.SECOND_REMINDER_TIME,
    val shouldPromptNotificationPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
) : UiState
