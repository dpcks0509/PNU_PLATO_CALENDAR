package pusan.university.plato_calendar.domain.entity

import pusan.university.plato_calendar.presentation.setting.model.AcademicNotificationHour
import java.time.LocalDate

data class AcademicScheduleAlarmInfo(
    val title: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val notificationsEnabled: Boolean = false,
    val startDateHour: AcademicNotificationHour = AcademicNotificationHour.NONE,
    val endDateHour: AcademicNotificationHour = AcademicNotificationHour.NONE,
    val notificationBaseId: Int? = null,
) {
    companion object {
        fun generateKey(title: String, startAt: LocalDate, endAt: LocalDate): String =
            "$title|$startAt|$endAt"
    }
}
