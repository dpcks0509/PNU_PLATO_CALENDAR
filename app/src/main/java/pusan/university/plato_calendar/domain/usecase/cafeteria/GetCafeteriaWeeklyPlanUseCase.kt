package pusan.university.plato_calendar.domain.usecase.cafeteria

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import javax.inject.Inject

class GetCafeteriaWeeklyPlanUseCase
    @Inject
    constructor(
        private val cafeteriaRepository: CafeteriaRepository,
    ) {
        suspend operator fun invoke(cafeteria: Cafeteria): ApiResult<CafeteriaWeeklyPlan> =
            cafeteriaRepository.getCafeteriaWeeklyPlan(cafeteria)
    }
