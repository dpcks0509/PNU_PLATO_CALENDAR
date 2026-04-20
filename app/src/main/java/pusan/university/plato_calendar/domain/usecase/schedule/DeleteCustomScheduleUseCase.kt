package pusan.university.plato_calendar.domain.usecase.schedule

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.repository.ScheduleAlarmRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import javax.inject.Inject

class DeleteCustomScheduleUseCase
@Inject
constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleAlarmRepository: ScheduleAlarmRepository,
) {
    suspend operator fun invoke(id: Long): ApiResult<Unit> {
        val result = scheduleRepository.deleteCustomSchedule(id)
        if (result is ApiResult.Success) {
            scheduleAlarmRepository.updateCompletion(id, false)
        }
        return result
    }
}
