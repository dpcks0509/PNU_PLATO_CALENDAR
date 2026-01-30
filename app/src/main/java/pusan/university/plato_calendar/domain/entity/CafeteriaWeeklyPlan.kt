package pusan.university.plato_calendar.domain.entity

data class CafeteriaWeeklyPlan(
    val cafeteria: Cafeteria,
    val weeklyPlans: List<DailyCafeteriaPlan>,
)
