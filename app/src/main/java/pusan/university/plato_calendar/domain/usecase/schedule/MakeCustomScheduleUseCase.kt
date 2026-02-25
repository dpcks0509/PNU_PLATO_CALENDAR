package pusan.university.plato_calendar.domain.usecase.schedule

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import javax.inject.Inject

class MakeCustomScheduleUseCase
    @Inject
    constructor(
        private val scheduleRepository: ScheduleRepository,
    ) {
        suspend operator fun invoke(newSchedule: NewSchedule): ApiResult<Long> =
            scheduleRepository.makeCustomSchedule(newSchedule)
    }
