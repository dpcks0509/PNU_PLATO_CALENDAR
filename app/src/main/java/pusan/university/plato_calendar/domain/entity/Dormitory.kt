package pusan.university.plato_calendar.domain.entity

enum class Dormitory(val title: String, val siteId: String, val campusId: String, val tabId: String?) {
    JILLI(title = "부산 진리관", siteId = "pdorm", campusId = "000000000000561", tabId = "pDormTab1"),
    UNGBEE(title = "부산 웅비관", siteId = "pdorm", campusId = "000000000000561", tabId = "pDormTab2"),
    JAYU(title = "부산 자유관", siteId = "pdorm", campusId = "000000000000561", tabId = "pDormTab3"),
    MILYANG(title = "밀양 기숙사", siteId = "mdorm", campusId = "000000000000628", tabId = null),
    YANGSAN(title = "양산 기숙사", siteId = "ydorm", campusId = "000000000000596", tabId = null);

    fun operatingTime(mealType: DormitoryMealType): String? = when (this) {
        JILLI, UNGBEE, JAYU -> when (mealType) {
            DormitoryMealType.EARLY_BREAKFAST -> "07:00-08:00"
            DormitoryMealType.BREAKFAST -> "07:00-09:00"
            DormitoryMealType.LUNCH -> "12:00-14:00"
            DormitoryMealType.DINNER -> "17:30-19:00"
        }

        MILYANG -> when (mealType) {
            DormitoryMealType.LUNCH -> "11:40-13:20"
            DormitoryMealType.DINNER -> "17:30-19:00"
            else -> null
        }

        YANGSAN -> when (mealType) {
            DormitoryMealType.EARLY_BREAKFAST -> "07:00-08:00"
            DormitoryMealType.BREAKFAST -> "07:20-08:50"
            DormitoryMealType.LUNCH -> "12:00-13:30"
            DormitoryMealType.DINNER -> "18:00-19:30"
        }
    }
}