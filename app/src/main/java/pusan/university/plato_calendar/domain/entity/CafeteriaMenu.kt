package pusan.university.plato_calendar.domain.entity

data class CafeteriaMenu(
    val date: String,
    val day: String,
    val mealType: MealType,
    val isOperating: Boolean,
    val notOperatingReason: String?,
    val operatingTime: String?,
    val courseName: String?,
    val price: String?,
    val dishes: String?,
)
