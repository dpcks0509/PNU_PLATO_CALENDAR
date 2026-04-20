package pusan.university.plato_calendar.presentation.setting.model

enum class NotificationTime(
    val label: String,
) {
    NONE("없음"),
    ON_TIME("이벤트 시간"),
    ONE_HOUR("1시간 전"),
    TWO_HOURS("2시간 전"),
    SIX_HOURS("6시간 전"),
    TWELVE_HOURS("12시간 전"),
    ONE_DAY("1일 전"),
    TWO_DAYS("2일 전"),
    THREE_DAYS("3일 전"),
    ONE_WEEK("1주 전"),
    ;

    companion object {
        fun NotificationTime.getReminderTime(): String =
            when (this) {
                ON_TIME -> "지금"
                else -> this.label
            }

        val FIRST_REMINDER_TIME = ONE_HOUR
        val SECOND_REMINDER_TIME = ONE_DAY
    }
}
