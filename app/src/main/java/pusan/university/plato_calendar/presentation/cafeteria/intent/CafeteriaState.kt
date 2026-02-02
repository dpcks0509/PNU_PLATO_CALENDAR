package pusan.university.plato_calendar.presentation.cafeteria.intent

import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaWeeklyPlan
import pusan.university.plato_calendar.presentation.common.base.UiState

data class CafeteriaState(
    val cafeteriaWeeklyPlans: List<CafeteriaWeeklyPlan> = emptyList(),
    val selectedCafeteria: Cafeteria = Cafeteria.GEUMJEONG_STUDENT,
) : UiState {
    fun getWeeklyPlanByCafeteria(cafeteria: Cafeteria): CafeteriaWeeklyPlan =
        cafeteriaWeeklyPlans.find { cafeteriaWeeklyPlan -> cafeteriaWeeklyPlan.cafeteria == cafeteria }
            ?: CafeteriaWeeklyPlan(cafeteria, "", emptyList())
}
