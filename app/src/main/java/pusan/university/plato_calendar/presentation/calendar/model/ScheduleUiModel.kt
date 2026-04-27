package pusan.university.plato_calendar.presentation.calendar.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pusan.university.plato_calendar.domain.entity.Schedule.AcademicSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import pusan.university.plato_calendar.presentation.util.extension.formatTimeWithMidnightSpecialCase
import pusan.university.plato_calendar.presentation.util.theme.CalendarFlamingo
import pusan.university.plato_calendar.presentation.util.theme.CalendarLavender
import pusan.university.plato_calendar.presentation.util.theme.CalendarSage
import pusan.university.plato_calendar.presentation.util.theme.MediumGray
import java.time.LocalDate
import java.time.LocalDateTime

sealed class ScheduleUiModel {
    abstract val title: String
    abstract val color: Color @Composable get

    data class AcademicScheduleUiModel(
        override val title: String,
        val startAt: LocalDate,
        val endAt: LocalDate,
        val notificationsEnabled: Boolean = false,
        val id: Long = 0L,
    ) : ScheduleUiModel() {
        override val color: Color @Composable get() = CalendarLavender

        constructor(domain: AcademicSchedule) : this(
            title = domain.title,
            startAt = domain.startAt,
            endAt = domain.endAt,
        )
    }

    sealed class PersonalScheduleUiModel : ScheduleUiModel() {
        abstract val id: Long
        abstract override val title: String
        abstract val description: String?
        abstract val startAt: LocalDateTime
        abstract val endAt: LocalDateTime
        abstract val isCompleted: Boolean
        abstract val notificationsEnabled: Boolean
        abstract val firstReminderTime: NotificationTime
        abstract val secondReminderTime: NotificationTime
        abstract val isCustomized: Boolean

        val deadLine: String
            get() = endAt.formatTimeWithMidnightSpecialCase()

        data class CourseScheduleUiModel(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
            override val isCompleted: Boolean,
            override val notificationsEnabled: Boolean = true,
            override val firstReminderTime: NotificationTime = NotificationTime.FIRST_REMINDER_TIME,
            override val secondReminderTime: NotificationTime = NotificationTime.SECOND_REMINDER_TIME,
            override val isCustomized: Boolean = false,
            val courseName: String,
        ) : PersonalScheduleUiModel() {
            val titleWithCourseName: String get() = if (courseName.isEmpty()) title else "${courseName}_$title"

            constructor(domain: PersonalSchedule.CourseSchedule, courseName: String) : this(
                id = domain.id,
                title = domain.title,
                description = domain.description,
                startAt = domain.startAt,
                endAt = domain.endAt,
                isCompleted = domain.isCompleted,
                courseName = courseName,
            )

            override val color: Color
                @Composable get() =
                    if (!isCompleted) CalendarSage else MediumGray
        }

        data class CustomScheduleUiModel(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
            override val isCompleted: Boolean,
            override val notificationsEnabled: Boolean = true,
            override val firstReminderTime: NotificationTime = NotificationTime.FIRST_REMINDER_TIME,
            override val secondReminderTime: NotificationTime = NotificationTime.SECOND_REMINDER_TIME,
            override val isCustomized: Boolean = false,
        ) : PersonalScheduleUiModel() {
            constructor(domain: CustomSchedule) : this(
                id = domain.id,
                title = domain.title,
                description = domain.description,
                startAt = domain.startAt,
                endAt = domain.endAt,
                isCompleted = domain.isCompleted,
            )

            override val color: Color
                @Composable get() =
                    if (!isCompleted) CalendarFlamingo else MediumGray
        }
    }
}
