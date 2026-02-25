package pusan.university.plato_calendar.data.local.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.data.local.database.SettingsDataStore
import pusan.university.plato_calendar.domain.entity.AppSettings
import pusan.university.plato_calendar.domain.repository.AppSettingsRepository
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import javax.inject.Inject

class LocalAppSettingsRepository
    @Inject
    constructor(
        private val settingsDataStore: SettingsDataStore,
    ) : AppSettingsRepository {
        override fun getAppSettings(): Flow<AppSettings> = settingsDataStore.settings

        override suspend fun setNotificationsEnabled(enabled: Boolean) {
            settingsDataStore.setNotificationsEnabled(enabled)
        }

        override suspend fun setReminderTime(
            firstTime: NotificationTime,
            secondTime: NotificationTime,
        ) {
            settingsDataStore.setReminderTime(firstTime, secondTime)
        }

        override suspend fun setThemeMode(mode: ThemeMode) {
            settingsDataStore.setThemeMode(mode)
        }
    }
