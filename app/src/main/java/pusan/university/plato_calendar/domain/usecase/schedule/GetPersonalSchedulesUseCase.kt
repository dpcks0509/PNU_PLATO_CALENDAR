package pusan.university.plato_calendar.domain.usecase.schedule

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.repository.ScheduleAlarmRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetPersonalSchedulesUseCase
@Inject
constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleAlarmRepository: ScheduleAlarmRepository,
) {
    suspend operator fun invoke(sessKey: String): ApiResult<List<PersonalSchedule>> {
        val result = scheduleRepository.getPersonalSchedules(sessKey)
        if (result !is ApiResult.Success) return result

        val completedIds = scheduleAlarmRepository.getCompletedScheduleIds()

        val mappedSchedules =
            result.data.map { schedule ->
                when (schedule) {
                    is CourseSchedule -> schedule.copy(isCompleted = completedIds.contains(schedule.id))
                    is CustomSchedule -> schedule.copy(isCompleted = completedIds.contains(schedule.id))
                }
            }
        return ApiResult.Success(mappedSchedules)
    }
}
