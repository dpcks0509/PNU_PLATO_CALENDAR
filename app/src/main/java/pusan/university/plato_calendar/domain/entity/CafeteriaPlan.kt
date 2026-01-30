package pusan.university.plato_calendar.domain.entity

data class CafeteriaPlan(
    val mealType: MealType,
    val isOperating: Boolean,
    val notOperatingReason: String?,
    val operatingTime: String?,
    val courseTitle: String?,
    val menus: String?,
)
