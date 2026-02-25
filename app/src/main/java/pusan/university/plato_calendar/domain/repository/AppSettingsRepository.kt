package pusan.university.plato_calendar.domain.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.AppSettings
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode

interface AppSettingsRepository {
    fun getAppSettings(): Flow<AppSettings>

    suspend fun setNotificationsEnabled(enabled: Boolean)

    suspend fun setReminderTime(
        firstTime: NotificationTime,
        secondTime: NotificationTime,
    )

    suspend fun setThemeMode(mode: ThemeMode)
}
