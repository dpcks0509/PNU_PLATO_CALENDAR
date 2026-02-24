package pusan.university.plato_calendar.presentation.setting.intent

import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import pusan.university.plato_calendar.presentation.util.base.UiEvent

sealed interface SettingEvent : UiEvent {
    data class Login(val credentials: LoginCredentials) : SettingEvent

    data object Logout : SettingEvent

    data class UpdateNotificationsEnabled(
        val enabled: Boolean,
    ) : SettingEvent

    data class UpdateFirstReminderTime(
        val time: NotificationTime,
    ) : SettingEvent

    data class UpdateSecondReminderTime(
        val time: NotificationTime,
    ) : SettingEvent

    data class UpdateNotificationPermission(
        val granted: Boolean,
    ) : SettingEvent

    data class NavigateToWebView(
        val url: String,
    ) : SettingEvent

    data class UpdateTheme(
        val mode: ThemeMode,
    ) : SettingEvent
}
