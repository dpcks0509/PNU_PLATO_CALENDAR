package pusan.university.plato_calendar.presentation.setting.intent

import pusan.university.plato_calendar.presentation.util.base.UiState
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode

data class SettingState(
    val userInfo: String? = null,
    val notificationsEnabled: Boolean = false,
    val firstReminderTime: NotificationTime = NotificationTime.ONE_HOUR,
    val secondReminderTime: NotificationTime = NotificationTime.NONE,
    val shouldPromptNotificationPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
) : UiState
