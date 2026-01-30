package pusan.university.plato_calendar.domain.entity

data class DailyCafeteriaPlan(
    val date: String,
    val day: String,
    val dailyPlans: List<CafeteriaPlan>,
) {
    fun getDailyPlansByMealType(mealType: MealType): List<CafeteriaPlan> =
        dailyPlans.filter { cafeteriaMenu -> cafeteriaMenu.mealType == mealType }
}
