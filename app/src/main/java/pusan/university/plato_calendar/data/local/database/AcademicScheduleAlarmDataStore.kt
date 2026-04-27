package pusan.university.plato_calendar.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import pusan.university.plato_calendar.domain.entity.AcademicScheduleAlarmInfo
import pusan.university.plato_calendar.presentation.setting.model.AcademicNotificationHour
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

private val Context.academicScheduleAlarmDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "academic_schedule_alarm_settings")

class AcademicScheduleAlarmDataStore
@Inject
constructor(
    private val context: Context,
) {
    suspend fun getAlarmInfo(key: String): AcademicScheduleAlarmInfo? {
        val prefs = context.academicScheduleAlarmDataStore.data
            .handleIOException()
            .first()

        val enabledKey = enabledKey(key)
        if (prefs[enabledKey] == null) return null

        val title = prefs[titleKey(key)] ?: return null
        val startAt = prefs[startDateKey(key)]?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return null
        val endAt = prefs[endDateKey(key)]?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return null

        return AcademicScheduleAlarmInfo(
            title = title,
            startAt = startAt,
            endAt = endAt,
            notificationsEnabled = prefs[enabledKey] == "true",
            startDateHour = prefs[startHourKey(key)]?.toAcademicNotificationHour() ?: AcademicNotificationHour.NONE,
            endDateHour = prefs[endHourKey(key)]?.toAcademicNotificationHour() ?: AcademicNotificationHour.NONE,
            notificationBaseId = prefs[notificationBaseIdKey(key)],
        )
    }

    suspend fun getAllAlarmInfos(): List<AcademicScheduleAlarmInfo> {
        val prefs = context.academicScheduleAlarmDataStore.data
            .handleIOException()
            .first()

        return prefs.asMap().keys
            .filter { it.name.startsWith(PREFIX_ENABLED) }
            .mapNotNull { prefKey ->
                val key = prefKey.name.removePrefix(PREFIX_ENABLED)
                getAlarmInfo(key)
            }
    }

    suspend fun getOrCreateNotificationBaseId(key: String): Int {
        context.academicScheduleAlarmDataStore.edit { prefs ->
            if (prefs[notificationBaseIdKey(key)] != null) return@edit
            val nextId = (prefs[NEXT_NOTIFICATION_ID_KEY] ?: 1)
            prefs[notificationBaseIdKey(key)] = nextId
            prefs[NEXT_NOTIFICATION_ID_KEY] = nextId + 1
        }
        return getNotificationBaseId(key) ?: 1
    }

    suspend fun getNotificationBaseId(key: String): Int? {
        val prefs = context.academicScheduleAlarmDataStore.data
            .handleIOException()
            .first()
        return prefs[notificationBaseIdKey(key)]
    }

    suspend fun saveAlarmInfo(key: String, alarmInfo: AcademicScheduleAlarmInfo) {
        context.academicScheduleAlarmDataStore.edit { prefs ->
            prefs[titleKey(key)] = alarmInfo.title
            prefs[startDateKey(key)] = alarmInfo.startAt.toString()
            prefs[endDateKey(key)] = alarmInfo.endAt.toString()
            prefs[enabledKey(key)] = alarmInfo.notificationsEnabled.toString()
            prefs[startHourKey(key)] = alarmInfo.startDateHour.name
            prefs[endHourKey(key)] = alarmInfo.endDateHour.name
        }
    }

    private fun Flow<Preferences>.handleIOException() =
        this.let { flow ->
            flow {
                try {
                    flow.collect { emit(it) }
                } catch (e: IOException) {
                    emit(emptyPreferences())
                }
            }
        }

    private fun String.toAcademicNotificationHour(): AcademicNotificationHour =
        runCatching { AcademicNotificationHour.valueOf(this) }.getOrDefault(AcademicNotificationHour.NONE)

    private fun enabledKey(key: String) = stringPreferencesKey("$PREFIX_ENABLED$key")
    private fun titleKey(key: String) = stringPreferencesKey("$PREFIX_TITLE$key")
    private fun startDateKey(key: String) = stringPreferencesKey("$PREFIX_START_DATE$key")
    private fun endDateKey(key: String) = stringPreferencesKey("$PREFIX_END_DATE$key")
    private fun startHourKey(key: String) = stringPreferencesKey("$PREFIX_START_HOUR$key")
    private fun endHourKey(key: String) = stringPreferencesKey("$PREFIX_END_HOUR$key")
    private fun notificationBaseIdKey(key: String) = intPreferencesKey("$PREFIX_NOTIFICATION_ID$key")

    companion object {
        private const val PREFIX_ENABLED = "acad_enabled_"
        private const val PREFIX_TITLE = "acad_title_"
        private const val PREFIX_START_DATE = "acad_start_date_"
        private const val PREFIX_END_DATE = "acad_end_date_"
        private const val PREFIX_START_HOUR = "acad_start_hour_"
        private const val PREFIX_END_HOUR = "acad_end_hour_"
        private const val PREFIX_NOTIFICATION_ID = "acad_notification_id_"
        private val NEXT_NOTIFICATION_ID_KEY = intPreferencesKey("acad_next_notification_id")
    }
}
