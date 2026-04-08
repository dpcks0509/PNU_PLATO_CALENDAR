package pusan.university.plato_calendar.domain.entity

data class DormitoryMealInfo(
    val mealType: DormitoryMealType,
    val menus: String,
    val operatingTime: String? = null,
)
