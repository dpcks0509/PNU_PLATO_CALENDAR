package pusan.university.plato_calendar.presentation.util.manager

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import pusan.university.plato_calendar.domain.entity.AppSettings
import pusan.university.plato_calendar.domain.usecase.settings.GetAppSettingsUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetAutoUpdateScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetNotificationsEnabledUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetReminderTimeUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetThemeModeUseCase
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import pusan.university.plato_calendar.presentation.widget.receiver.CalendarWidgetReceiver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        getAppSettingsUseCase: GetAppSettingsUseCase,
        private val setAutoUpdateScheduleUseCase: SetAutoUpdateScheduleUseCase,
        private val setNotificationsEnabledUseCase: SetNotificationsEnabledUseCase,
        private val setReminderTimeUseCase: SetReminderTimeUseCase,
        private val setThemeModeUseCase: SetThemeModeUseCase,
    ) {
        val appSettings: Flow<AppSettings> = getAppSettingsUseCase()

        var initialSettings: AppSettings = AppSettings()
            private set

        suspend fun loadInitialSettings() {
            val autoUpdateSchedule = isWidgetAdded(context)
            setAutoUpdateSchedule(enabled = autoUpdateSchedule)
            initialSettings = appSettings.first()
        }

        suspend fun setAutoUpdateSchedule(enabled: Boolean) {
            setAutoUpdateScheduleUseCase(enabled)
        }

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

        private fun isWidgetAdded(context: Context): Boolean {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetProvider = ComponentName(context, CalendarWidgetReceiver::class.java)
            return appWidgetManager.getAppWidgetIds(widgetProvider).isNotEmpty()
        }
    }
