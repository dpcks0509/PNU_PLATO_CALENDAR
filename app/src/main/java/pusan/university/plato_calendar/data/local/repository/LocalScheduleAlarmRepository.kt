package pusan.university.plato_calendar.data.local.repository

import pusan.university.plato_calendar.data.local.database.ScheduleAlarmDataStore
import pusan.university.plato_calendar.domain.entity.ScheduleAlarmInfo
import pusan.university.plato_calendar.domain.repository.ScheduleAlarmRepository
import javax.inject.Inject

class LocalScheduleAlarmRepository
@Inject
constructor(
    private val scheduleAlarmDataStore: ScheduleAlarmDataStore,
) : ScheduleAlarmRepository {
    override suspend fun getAlarmInfo(scheduleId: Long): ScheduleAlarmInfo? =
        scheduleAlarmDataStore.getAlarmInfo(scheduleId)

    override suspend fun getAllAlarmInfos(): Map<Long, ScheduleAlarmInfo> =
        scheduleAlarmDataStore.getAllAlarmInfos()

    override suspend fun saveAlarmInfo(scheduleId: Long, alarmInfo: ScheduleAlarmInfo) {
        scheduleAlarmDataStore.saveAlarmInfo(scheduleId, alarmInfo)
    }

    override suspend fun updateCompletion(scheduleId: Long, isCompleted: Boolean) {
        scheduleAlarmDataStore.updateCompletion(scheduleId, isCompleted)
    }

    override suspend fun getCompletedScheduleIds(): Set<Long> =
        scheduleAlarmDataStore.getCompletedScheduleIds()
}
