package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.AcademicScheduleAlarmInfo

interface AcademicScheduleAlarmRepository {
    suspend fun getAlarmInfo(key: String): AcademicScheduleAlarmInfo?
    suspend fun getAllAlarmInfos(): List<AcademicScheduleAlarmInfo>
    suspend fun saveAlarmInfo(key: String, alarmInfo: AcademicScheduleAlarmInfo)
}
