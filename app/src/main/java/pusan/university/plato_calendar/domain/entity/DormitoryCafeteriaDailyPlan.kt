package pusan.university.plato_calendar.domain.entity

data class DormitoryCafeteriaDailyPlan(
    val date: String,
    val day: String,
    val mealInfos: List<DormitoryMealInfo>,
) {
    fun getMealInfo(mealType: DormitoryMealType): DormitoryMealInfo? =
        mealInfos.find { it.mealType == mealType }
}