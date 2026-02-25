package pusan.university.plato_calendar.domain.usecase.schedule

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.repository.CompletedScheduleRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetPersonalSchedulesUseCase
    @Inject
    constructor(
        private val scheduleRepository: ScheduleRepository,
        private val completedScheduleRepository: CompletedScheduleRepository,
    ) {
        suspend operator fun invoke(sessKey: String): ApiResult<List<PersonalSchedule>> {
            val result = scheduleRepository.getPersonalSchedules(sessKey)
            if (result !is ApiResult.Success) return result

            val completedIds =
                completedScheduleRepository.getCompletedScheduleIds()
                    .catch { emit(emptySet()) }
                    .first()

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
