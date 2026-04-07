package pusan.university.plato_calendar.domain.usecase.cafeteria

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.entity.DormitoryCafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.repository.DormitoryCafeteriaRepository
import javax.inject.Inject

class GetDormitoryCafeteriaWeeklyPlanUseCase
@Inject
constructor(
    private val dormitoryCafeteriaRepository: DormitoryCafeteriaRepository,
) {
    suspend operator fun invoke(dormitory: Dormitory): ApiResult<DormitoryCafeteriaWeeklyPlan> =
        dormitoryCafeteriaRepository.getWeeklyPlan(dormitory)
}
