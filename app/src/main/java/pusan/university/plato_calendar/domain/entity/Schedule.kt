package pusan.university.plato_calendar.domain.entity

import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import java.time.LocalDate
import java.time.LocalDateTime

sealed class Schedule {
    abstract val title: String

    data class AcademicSchedule(
        override val title: String,
        val startAt: LocalDate,
        val endAt: LocalDate,
    ) : Schedule()

    sealed class PersonalSchedule : Schedule() {
        abstract val id: Long
        abstract val description: String?
        abstract val startAt: LocalDateTime
        abstract val endAt: LocalDateTime
        abstract val isCompleted: Boolean

        data class CourseSchedule(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
            override val isCompleted: Boolean,
            val courseCode: String,
        ) : PersonalSchedule()

        data class CustomSchedule(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
            override val isCompleted: Boolean,
        ) : PersonalSchedule()
    }

    data class NewSchedule(
        override val title: String,
        val description: String?,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
        val notificationsEnabled: Boolean = true,
        val firstReminderTime: NotificationTime = NotificationTime.ONE_HOUR,
        val secondReminderTime: NotificationTime = NotificationTime.ONE_DAY,
    ) : Schedule()
}
