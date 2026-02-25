package pusan.university.plato_calendar.presentation.util.manager

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.AppSettings
import pusan.university.plato_calendar.domain.usecase.settings.GetAppSettingsUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetNotificationsEnabledUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetReminderTimeUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetThemeModeUseCase
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager
    @Inject
    constructor(
        getAppSettingsUseCase: GetAppSettingsUseCase,
        private val setNotificationsEnabledUseCase: SetNotificationsEnabledUseCase,
        private val setReminderTimeUseCase: SetReminderTimeUseCase,
        private val setThemeModeUseCase: SetThemeModeUseCase,
    ) {
        val appSettings: Flow<AppSettings> = getAppSettingsUseCase()

        suspend fun setNotificationsEnabled(enabled: Boolean) {
            setNotificationsEnabledUseCase(enabled)
        }

        suspend fun setReminderTimes(
            firstTime: NotificationTime,
            secondTime: NotificationTime,
        ) {
            setReminderTimeUseCase(firstTime, secondTime)
        }

        suspend fun setThemeMode(mode: ThemeMode) {
            setThemeModeUseCase(mode)
        }
    }
