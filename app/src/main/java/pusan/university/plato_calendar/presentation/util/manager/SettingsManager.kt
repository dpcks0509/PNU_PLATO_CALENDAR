package pusan.university.plato_calendar.presentation.util.manager

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.data.local.database.SettingsDataStore
import pusan.university.plato_calendar.domain.entity.AppSettings
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager
    @Inject
    constructor(
        private val settingsDataStore: SettingsDataStore,
    ) {
        val appSettings: Flow<AppSettings> = settingsDataStore.settings

        suspend fun setNotificationsEnabled(enabled: Boolean) {
            settingsDataStore.setNotificationsEnabled(enabled)
        }

        suspend fun setReminderTime(
            firstTime: NotificationTime,
            secondTime: NotificationTime,
        ) {
            settingsDataStore.setReminderTime(firstTime, secondTime)
        }

        suspend fun setThemeMode(mode: ThemeMode) {
            settingsDataStore.setThemeMode(mode)
        }
    }
