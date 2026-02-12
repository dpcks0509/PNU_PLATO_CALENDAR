package pusan.university.plato_calendar.presentation.calendar.intent

import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.YearMonth
import pusan.university.plato_calendar.presentation.common.base.UiEvent
import java.time.LocalDate

sealed interface CalendarEvent : UiEvent {
    data class Login(val credentials: LoginCredentials) : CalendarEvent

    data object MoveToToday : CalendarEvent

    data object Refresh : CalendarEvent

    data class MakeCustomSchedule(
        val schedule: NewSchedule,
    ) : CalendarEvent

    data class EditCustomSchedule(
        val schedule: CustomSchedule,
    ) : CalendarEvent

    data class DeleteCustomSchedule(
        val id: Long,
    ) : CalendarEvent

    data class UpdateSelectedDate(
        val date: LocalDate,
    ) : CalendarEvent

    data class UpdateCurrentYearMonth(
        val yearMonth: YearMonth,
    ) : CalendarEvent

    data class TogglePersonalScheduleCompletion(
        val id: Long,
        val isCompleted: Boolean,
    ) : CalendarEvent

    data class ShowScheduleBottomSheet(
        val schedule: ScheduleUiModel? = null,
    ) : CalendarEvent

    data class ShowScheduleBottomSheetById(
        val scheduleId: Long,
    ) : CalendarEvent
}
