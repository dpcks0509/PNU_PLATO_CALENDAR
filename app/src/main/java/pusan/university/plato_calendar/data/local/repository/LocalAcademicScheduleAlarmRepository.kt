package pusan.university.plato_calendar.data.local.repository

import pusan.university.plato_calendar.data.local.database.AcademicScheduleAlarmDataStore
import pusan.university.plato_calendar.domain.entity.AcademicScheduleAlarmInfo
import pusan.university.plato_calendar.domain.repository.AcademicScheduleAlarmRepository
import javax.inject.Inject

class LocalAcademicScheduleAlarmRepository
@Inject
constructor(
    private val dataStore: AcademicScheduleAlarmDataStore,
) : AcademicScheduleAlarmRepository {
    override suspend fun getAlarmInfo(key: String): AcademicScheduleAlarmInfo? =
        dataStore.getAlarmInfo(key)

    override suspend fun getAllAlarmInfos(): List<AcademicScheduleAlarmInfo> =
        dataStore.getAllAlarmInfos()

    override suspend fun saveAlarmInfo(key: String, alarmInfo: AcademicScheduleAlarmInfo) {
        dataStore.saveAlarmInfo(key, alarmInfo)
    }
}
