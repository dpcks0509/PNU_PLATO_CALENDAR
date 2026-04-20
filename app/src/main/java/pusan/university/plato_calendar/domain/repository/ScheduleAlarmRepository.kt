package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.ScheduleAlarmInfo

interface ScheduleAlarmRepository {
    suspend fun getAlarmInfo(scheduleId: Long): ScheduleAlarmInfo?
    suspend fun getAllAlarmInfos(): Map<Long, ScheduleAlarmInfo>
    suspend fun saveAlarmInfo(scheduleId: Long, alarmInfo: ScheduleAlarmInfo)
    suspend fun updateCompletion(scheduleId: Long, isCompleted: Boolean)
    suspend fun getCompletedScheduleIds(): Set<Long>
}
