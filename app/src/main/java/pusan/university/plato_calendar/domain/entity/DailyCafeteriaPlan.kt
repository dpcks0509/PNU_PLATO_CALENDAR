package pusan.university.plato_calendar.domain.entity

data class DailyCafeteriaPlan(
    val date: String,
    val day: String,
    val mealInfos: List<MealInfo> = emptyList(),
) {
    fun getMealInfo(mealType: MealType): MealInfo? =
        mealInfos.find { it.mealType == mealType }
}
