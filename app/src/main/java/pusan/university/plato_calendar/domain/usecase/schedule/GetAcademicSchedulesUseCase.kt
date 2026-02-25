package pusan.university.plato_calendar.domain.usecase.schedule

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Schedule.AcademicSchedule
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetAcademicSchedulesUseCase
    @Inject
    constructor(
        private val scheduleRepository: ScheduleRepository,
    ) {
        suspend operator fun invoke(): ApiResult<List<AcademicSchedule>> =
            scheduleRepository.getAcademicSchedules()
    }
