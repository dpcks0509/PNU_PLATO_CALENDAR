package pusan.university.plato_calendar.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import pusan.university.plato_calendar.domain.entity.ScheduleAlarmInfo
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import java.io.IOException
import javax.inject.Inject

private val Context.scheduleAlarmDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "schedule_alarm_settings")

private val Context.legacyCompletedSchedulesDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "completed_schedules")

private val KEY_LEGACY_COMPLETED_IDS = stringSetPreferencesKey("completed_schedule_ids")

class ScheduleAlarmDataStore
@Inject
constructor(
    private val context: Context,
) {
    suspend fun getAlarmInfo(scheduleId: Long): ScheduleAlarmInfo? {
        val prefs = context.scheduleAlarmDataStore.data
            .handleIOException()
            .first()

        val enabledKey = enabledKey(scheduleId)
        if (prefs[enabledKey] == null) return null

        return ScheduleAlarmInfo(
            isCompleted = prefs[completedKey(scheduleId)] == "true",
            notificationsEnabled = prefs[enabledKey] == "true",
            firstReminderTime = prefs[firstReminderKey(scheduleId)]?.toNotificationTime() ?: NotificationTime.FIRST_REMINDER_TIME,
            secondReminderTime = prefs[secondReminderKey(scheduleId)]?.toNotificationTime() ?: NotificationTime.SECOND_REMINDER_TIME,
            isCustomized = prefs[customizedKey(scheduleId)] == "true",
        )
    }

    suspend fun getAllAlarmInfos(): Map<Long, ScheduleAlarmInfo> {
        migrateIfNeeded()

        val prefs = context.scheduleAlarmDataStore.data
            .handleIOException()
            .first()

        val result = mutableMapOf<Long, ScheduleAlarmInfo>()

        prefs.asMap().keys
            .filter { it.name.startsWith(PREFIX_ENABLED) }
            .forEach { key ->
                val idStr = key.name.removePrefix(PREFIX_ENABLED)
                val scheduleId = idStr.toLongOrNull() ?: return@forEach
                val info = getAlarmInfo(scheduleId) ?: return@forEach
                result[scheduleId] = info
            }

        return result
    }

    suspend fun saveAlarmInfo(scheduleId: Long, alarmInfo: ScheduleAlarmInfo) {
        context.scheduleAlarmDataStore.edit { prefs ->
            prefs[completedKey(scheduleId)] = alarmInfo.isCompleted.toString()
            prefs[enabledKey(scheduleId)] = alarmInfo.notificationsEnabled.toString()
            prefs[firstReminderKey(scheduleId)] = alarmInfo.firstReminderTime.name
            prefs[secondReminderKey(scheduleId)] = alarmInfo.secondReminderTime.name
            prefs[customizedKey(scheduleId)] = alarmInfo.isCustomized.toString()
        }
    }

    suspend fun updateCompletion(scheduleId: Long, isCompleted: Boolean) {
        context.scheduleAlarmDataStore.edit { prefs ->
            val enabledKey = enabledKey(scheduleId)
            if (prefs[enabledKey] == null) {
                prefs[enabledKey] = "true"
                prefs[firstReminderKey(scheduleId)] = NotificationTime.FIRST_REMINDER_TIME.name
                prefs[secondReminderKey(scheduleId)] = NotificationTime.SECOND_REMINDER_TIME.name
            }
            prefs[completedKey(scheduleId)] = isCompleted.toString()
        }
    }

    suspend fun getCompletedScheduleIds(): Set<Long> {
        val prefs = context.scheduleAlarmDataStore.data
            .handleIOException()
            .first()

        return prefs.asMap().keys
            .filter { it.name.startsWith(PREFIX_COMPLETED) }
            .mapNotNull { key ->
                val idStr = key.name.removePrefix(PREFIX_COMPLETED)
                val id = idStr.toLongOrNull() ?: return@mapNotNull null
                if (prefs[completedKey(id)] == "true") id else null
            }
            .toSet()
    }

    private suspend fun migrateIfNeeded() {
        val prefs = context.scheduleAlarmDataStore.data.handleIOException().first()
        if (prefs[KEY_MIGRATION_DONE] == true) return

        val legacyPrefs = context.legacyCompletedSchedulesDataStore.data
            .handleIOException()
            .first()
        val completedIds = legacyPrefs[KEY_LEGACY_COMPLETED_IDS]
            ?.mapNotNull { it.toLongOrNull() }
            ?.toSet()
            ?: emptySet()

        context.scheduleAlarmDataStore.edit { p ->
            completedIds.forEach { id ->
                val enabledKey = enabledKey(id)
                if (p[enabledKey] == null) {
                    p[enabledKey] = "true"
                    p[firstReminderKey(id)] = NotificationTime.FIRST_REMINDER_TIME.name
                    p[secondReminderKey(id)] = NotificationTime.SECOND_REMINDER_TIME.name
                }
                p[completedKey(id)] = "true"
            }
            p[KEY_MIGRATION_DONE] = true
        }
    }

    private fun kotlinx.coroutines.flow.Flow<Preferences>.handleIOException() =
        this.let { flow ->
            kotlinx.coroutines.flow.flow {
                try {
                    flow.collect { emit(it) }
                } catch (e: IOException) {
                    emit(emptyPreferences())
                }
            }
        }

    private fun String.toNotificationTime(): NotificationTime =
        runCatching { NotificationTime.valueOf(this) }.getOrDefault(NotificationTime.NONE)

    private fun enabledKey(id: Long) = stringPreferencesKey("$PREFIX_ENABLED$id")
    private fun firstReminderKey(id: Long) = stringPreferencesKey("$PREFIX_FIRST$id")
    private fun secondReminderKey(id: Long) = stringPreferencesKey("$PREFIX_SECOND$id")
    private fun completedKey(id: Long) = stringPreferencesKey("$PREFIX_COMPLETED$id")
    private fun customizedKey(id: Long) = stringPreferencesKey("$PREFIX_CUSTOMIZED$id")

    companion object {
        private const val PREFIX_ENABLED = "alarm_enabled_"
        private const val PREFIX_FIRST = "alarm_first_"
        private const val PREFIX_SECOND = "alarm_second_"
        private const val PREFIX_COMPLETED = "alarm_completed_"
        private const val PREFIX_CUSTOMIZED = "alarm_customized_"
        private val KEY_MIGRATION_DONE = booleanPreferencesKey("migration_v1_done")
    }
}
