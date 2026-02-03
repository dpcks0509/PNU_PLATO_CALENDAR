package pusan.university.plato_calendar.domain.entity

data class MealInfo(
    val mealType: MealType,
    val operationInfo: OperationInfo,
    val courseMenus: List<CourseMenu>,
)
