package pusan.university.plato_calendar.presentation.calendar.model

import pusan.university.plato_calendar.presentation.calendar.model.DayOfWeekUiModel.Companion.isWeekend
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import java.time.LocalDate

data class DaySchedule(
    val date: LocalDate,
    val isToday: Boolean,
    val isSelected: Boolean,
    val isInMonth: Boolean,
    val schedules: List<ScheduleUiModel>,
) {
    val isWeekend: Boolean = date.dayOfWeek.isWeekend()

    val visibleSchedules: List<ScheduleUiModel> =
        schedules
            .filter { schedule ->
                !(schedule is PersonalScheduleUiModel && schedule.isCompleted)
            }.sortedBy { schedule ->
                when (schedule) {
                    is PersonalScheduleUiModel -> 0
                    is AcademicScheduleUiModel -> 1
                }
            }.take(MAX_SCHEDULES_SIZE)

    companion object {
        private const val MAX_SCHEDULES_SIZE = 5
    }
}
