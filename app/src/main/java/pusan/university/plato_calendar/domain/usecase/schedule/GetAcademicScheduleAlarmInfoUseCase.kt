package pusan.university.plato_calendar.domain.usecase.schedule

import pusan.university.plato_calendar.domain.entity.AcademicScheduleAlarmInfo
import pusan.university.plato_calendar.domain.repository.AcademicScheduleAlarmRepository
import javax.inject.Inject

class GetAcademicScheduleAlarmInfoUseCase
@Inject
constructor(
    private val repository: AcademicScheduleAlarmRepository,
) {
    suspend operator fun invoke(key: String): AcademicScheduleAlarmInfo? =
        repository.getAlarmInfo(key)
}
