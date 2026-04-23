package pusan.university.plato_calendar.presentation.setting.model

enum class AcademicNotificationHour(
    val label: String,
    val hour: Int,
) {
    NONE("없음", -1),
    HOUR_00("00시", 0),
    HOUR_09("09시", 9),
    HOUR_12("12시", 12),
    HOUR_15("15시", 15),
    HOUR_18("18시", 18),
    HOUR_22("22시", 22),
}
