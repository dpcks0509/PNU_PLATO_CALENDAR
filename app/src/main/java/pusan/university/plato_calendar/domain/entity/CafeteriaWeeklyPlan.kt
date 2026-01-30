package pusan.university.plato_calendar.domain.entity

data class CafeteriaWeeklyPlan(
    val cafeteria: Cafeteria,
    val notice: String,
    val weeklyPlans: List<DailyCafeteriaPlan>,
)
