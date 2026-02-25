package pusan.university.plato_calendar.domain.usecase.schedule

import pusan.university.plato_calendar.domain.repository.CompletedScheduleRepository
import javax.inject.Inject

class MarkScheduleAsCompletedUseCase
    @Inject
    constructor(
        private val completedScheduleRepository: CompletedScheduleRepository,
    ) {
        suspend operator fun invoke(id: Long) = completedScheduleRepository.addCompletedSchedule(id)
    }
