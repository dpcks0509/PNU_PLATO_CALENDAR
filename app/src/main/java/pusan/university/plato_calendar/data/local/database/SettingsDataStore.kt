package pusan.university.plato_calendar.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pusan.university.plato_calendar.domain.entity.AppSettings
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import java.io.IOException
import javax.inject.Inject

class SettingsDataStore
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME)

        val settings: Flow<AppSettings> =
            context
                .dataStore
                .data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }.map { preferences ->
                    val notificationsEnabled = preferences[KEY_NOTIFICATIONS_ENABLED] ?: false
                    val firstReminderName =
                        preferences[KEY_FIRST_REMINDER_TIME_NAME] ?: NotificationTime.ONE_HOUR.name
                    val secondReminderName =
                        preferences[KEY_SECOND_REMINDER_TIME_NAME] ?: NotificationTime.ONE_DAY.name
                    val themeModeName =
                        preferences[KEY_THEME_MODE_NAME] ?: ThemeMode.SYSTEM.name

                    val firstReminderTime =
                        runCatching { NotificationTime.valueOf(firstReminderName) }
                            .getOrDefault(NotificationTime.ONE_HOUR)
                    val secondReminderTime =
                        runCatching { NotificationTime.valueOf(secondReminderName) }
                            .getOrDefault(NotificationTime.ONE_DAY)
                    val themeMode =
                        runCatching { ThemeMode.valueOf(themeModeName) }
                            .getOrDefault(ThemeMode.SYSTEM)

                    AppSettings(
                        notificationsEnabled = notificationsEnabled,
                        firstReminderTime = firstReminderTime,
                        secondReminderTime = secondReminderTime,
                        themeMode = themeMode,
                    )
                }

        suspend fun setNotificationsEnabled(enabled: Boolean) {
            context.dataStore.edit { prefs ->
                prefs[KEY_NOTIFICATIONS_ENABLED] = enabled
            }
        }

        suspend fun setReminderTime(
            firstTime: NotificationTime,
            secondTime: NotificationTime,
        ) {
            context.dataStore.edit { prefs ->
                prefs[KEY_FIRST_REMINDER_TIME_NAME] = firstTime.name
                prefs[KEY_SECOND_REMINDER_TIME_NAME] = secondTime.name
            }
        }

        suspend fun setThemeMode(mode: ThemeMode) {
            context.dataStore.edit { prefs ->
                prefs[KEY_THEME_MODE_NAME] = mode.name
            }
        }

        companion object {
            private const val SETTINGS_NAME = "app_settings"
            private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
            private val KEY_FIRST_REMINDER_TIME_NAME = stringPreferencesKey("first_reminder_time_name")
            private val KEY_SECOND_REMINDER_TIME_NAME =
                stringPreferencesKey("second_reminder_time_name")
            private val KEY_THEME_MODE_NAME = stringPreferencesKey("theme_mode_name")
        }
    }
