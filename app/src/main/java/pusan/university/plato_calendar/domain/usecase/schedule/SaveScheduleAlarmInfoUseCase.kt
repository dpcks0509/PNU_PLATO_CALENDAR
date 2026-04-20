package pusan.university.plato_calendar.domain.usecase.schedule

import pusan.university.plato_calendar.domain.entity.ScheduleAlarmInfo
import pusan.university.plato_calendar.domain.repository.ScheduleAlarmRepository
import javax.inject.Inject

class SaveScheduleAlarmInfoUseCase
@Inject
constructor(
    private val repository: ScheduleAlarmRepository,
) {
    suspend operator fun invoke(scheduleId: Long, alarmInfo: ScheduleAlarmInfo) {
        repository.saveAlarmInfo(scheduleId, alarmInfo)
    }
}
