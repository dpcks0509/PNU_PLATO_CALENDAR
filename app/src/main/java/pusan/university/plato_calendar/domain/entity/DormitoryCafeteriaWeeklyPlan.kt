package pusan.university.plato_calendar.domain.entity

data class DormitoryCafeteriaWeeklyPlan(
    val dormitory: Dormitory,
    val notice: String,
    val weeklyPlans: List<DormitoryCafeteriaDailyPlan>,
)
