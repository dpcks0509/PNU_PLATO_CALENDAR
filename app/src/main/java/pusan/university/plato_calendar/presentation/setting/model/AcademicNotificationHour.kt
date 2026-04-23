package pusan.university.plato_calendar.presentation.setting.model

enum class AcademicNotificationHour(
    val label: String,
    val hour: Int,
) {
    NONE("없음", -1),
    HOUR_00("00:00", 0),
    HOUR_08("08:00", 8),
    HOUR_10("10:00", 10),
    HOUR_12("12:00", 12),
    HOUR_14("14:00", 14),
    HOUR_16("16:00", 16),
    HOUR_18("18:00", 18),
    HOUR_20("20:00", 20),
    HOUR_22("22:00", 22),
}
