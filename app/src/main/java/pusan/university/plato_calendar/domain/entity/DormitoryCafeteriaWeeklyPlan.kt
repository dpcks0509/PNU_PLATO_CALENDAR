package pusan.university.plato_calendar.domain.entity

data class DormitoryCafeteriaWeeklyPlan(
    val dormitory: Dormitory,
    val weeklyPlans: List<DormitoryCafeteriaDailyPlan>,
)
