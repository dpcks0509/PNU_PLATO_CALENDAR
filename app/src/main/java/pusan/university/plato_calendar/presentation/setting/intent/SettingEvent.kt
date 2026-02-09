package pusan.university.plato_calendar.presentation.setting.intent

import pusan.university.plato_calendar.presentation.common.base.UiEvent
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime

sealed interface SettingEvent : UiEvent {
    data object ShowLoginDialog : SettingEvent

    data object HideLoginDialog : SettingEvent

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
}
