package pusan.university.plato_calendar.domain.usecase.schedule

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import javax.inject.Inject

class EditPersonalScheduleUseCase
    @Inject
    constructor(
        private val scheduleRepository: ScheduleRepository,
    ) {
        suspend operator fun invoke(personalSchedule: PersonalSchedule): ApiResult<Unit> =
            scheduleRepository.editPersonalSchedule(personalSchedule)
    }
