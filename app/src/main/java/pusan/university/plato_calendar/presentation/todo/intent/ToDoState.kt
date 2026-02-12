package pusan.university.plato_calendar.presentation.todo.intent

import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.base.UiState
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.schedule.content.ScheduleBottomSheetContent
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.ScheduleDialogContent
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class ToDoState(
    val today: LocalDateTime,
    val schedules: List<ScheduleUiModel> = emptyList(),
    val scheduleBottomSheetContent: ScheduleBottomSheetContent? = null,
    val scheduleDialogContent: ScheduleDialogContent? = null,
) : UiState {
    private val validSchedules =
        schedules
            .filter { schedule ->
                when (schedule) {
                    is AcademicScheduleUiModel -> !schedule.endAt.isBefore(today.toLocalDate())
                    is PersonalScheduleUiModel -> schedule.endAt.isAfter(today) && !schedule.isCompleted
                }
            }.sortedWith(
                compareBy(
                    { schedule ->
                        when (schedule) {
                            is AcademicScheduleUiModel -> schedule.endAt.atStartOfDay()
                            is PersonalScheduleUiModel -> schedule.endAt
                        }
                    },
                    { schedule ->
                        when (schedule) {
                            is AcademicScheduleUiModel -> schedule.startAt.atStartOfDay()
                            else -> null
                        }
                    },
                ),
            )

    val within7Days =
        validSchedules
            .filterIsInstance<PersonalScheduleUiModel>()
            .filter { schedule ->
                val daysUntilEnd = ChronoUnit.DAYS.between(today.toLocalDate(), schedule.endAt.toLocalDate())
                daysUntilEnd in 0..7
            }

    val completedSchedules =
        schedules
            .filterIsInstance<PersonalScheduleUiModel>()
            .filter { it.endAt.isAfter(today) && it.isCompleted }
            .sortedByDescending { it.endAt }

    val courseSchedules = validSchedules.filterIsInstance<CourseScheduleUiModel>()

    val customSchedules = validSchedules.filterIsInstance<CustomScheduleUiModel>()

    val academicSchedules = validSchedules.filterIsInstance<AcademicScheduleUiModel>()
}
