package pusan.university.plato_calendar.presentation.cafeteria.intent

import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaDailyPlan
import pusan.university.plato_calendar.domain.entity.CafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.entity.DormitoryCafeteriaDailyPlan
import pusan.university.plato_calendar.domain.entity.DormitoryCafeteriaWeeklyPlan
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab
import pusan.university.plato_calendar.presentation.util.base.UiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CafeteriaState(
    val today: LocalDate,
    val selectedDate: LocalDate,
    val selectedTab: CafeteriaTab = CafeteriaTab.CAMPUS,
    val selectedCafeteria: Cafeteria = Cafeteria.GEUMJEONG_STUDENT,
    val selectedDormitory: Dormitory = Dormitory.JILLI,
    val cafeteriaWeeklyPlans: Map<Cafeteria, CafeteriaWeeklyPlan> = emptyMap(),
    val dormWeeklyPlans: Map<Dormitory, DormitoryCafeteriaWeeklyPlan> = emptyMap(),
) : UiState {
    fun getWeeklyPlanByCafeteria(cafeteria: Cafeteria): CafeteriaWeeklyPlan =
        cafeteriaWeeklyPlans[cafeteria] ?: CafeteriaWeeklyPlan(cafeteria, "", emptyList())

    fun getWeeklyPlanByDormitory(dormitory: Dormitory): DormitoryCafeteriaWeeklyPlan =
        dormWeeklyPlans[dormitory] ?: DormitoryCafeteriaWeeklyPlan(dormitory, "", emptyList())

    fun getCurrentDailyPlan(): CafeteriaDailyPlan? {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val selectedDateString = selectedDate.format(formatter)
        return getWeeklyPlanByCafeteria(selectedCafeteria).weeklyPlans.find { it.date == selectedDateString }
    }

    fun getCurrentDormDailyPlan(): DormitoryCafeteriaDailyPlan? {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val selectedDateString = selectedDate.format(formatter)
        return getWeeklyPlanByDormitory(selectedDormitory).weeklyPlans.find { it.date == selectedDateString }
    }

    fun getWeekStartDate(): LocalDate? {
        return getWeeklyPlanByCafeteria(selectedCafeteria).weeklyPlans.firstOrNull()?.let { parseDateString(it.date) }
    }

    fun getWeekEndDate(): LocalDate? =
        getWeeklyPlanByCafeteria(selectedCafeteria).weeklyPlans.lastOrNull()?.let { parseDateString(it.date) }

    private fun parseDateString(dateString: String): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return runCatching { LocalDate.parse(dateString, formatter) }.getOrNull()
    }
}
