package pusan.university.plato_calendar.presentation.cafeteria.intent

import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.entity.DailyCafeteriaPlan
import pusan.university.plato_calendar.presentation.common.base.UiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CafeteriaState(
    val today: LocalDate,
    val selectedDate: LocalDate,
    val cafeteriaWeeklyPlans: List<CafeteriaWeeklyPlan> = emptyList(),
    val selectedCafeteria: Cafeteria = Cafeteria.GEUMJEONG_STUDENT,
) : UiState {
    fun getWeeklyPlanByCafeteria(cafeteria: Cafeteria): CafeteriaWeeklyPlan =
        cafeteriaWeeklyPlans.find { cafeteriaWeeklyPlan -> cafeteriaWeeklyPlan.cafeteria == cafeteria }
            ?: CafeteriaWeeklyPlan(cafeteria, "", emptyList())

    fun getCurrentDailyPlan(): DailyCafeteriaPlan? {
        val weeklyPlan = getWeeklyPlanByCafeteria(selectedCafeteria)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val selectedDateString = selectedDate.format(formatter)

        return weeklyPlan.weeklyPlans.find { dailyPlan ->
            dailyPlan.date == selectedDateString
        }
    }
    
    fun getWeekStartDate(): LocalDate? {
        val weeklyPlan = getWeeklyPlanByCafeteria(selectedCafeteria)
        return weeklyPlan.weeklyPlans.firstOrNull()?.let { dailyPlan ->
            parseDateString(dailyPlan.date)
        }
    }
    
    fun getWeekEndDate(): LocalDate? {
        val weeklyPlan = getWeeklyPlanByCafeteria(selectedCafeteria)
        return weeklyPlan.weeklyPlans.lastOrNull()?.let { dailyPlan ->
            parseDateString(dailyPlan.date)
        }
    }
    
    private fun parseDateString(dateString: String): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            LocalDate.parse(dateString, formatter)
        } catch (_: Exception) {
            null
        }
    }
}
