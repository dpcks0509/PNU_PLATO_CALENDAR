package pusan.university.plato_calendar.presentation.calendar.intent

import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.YearMonth
import pusan.university.plato_calendar.presentation.common.base.UiState
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent
import java.time.LocalDate

data class CalendarState(
    val today: LocalDate,
    val selectedDate: LocalDate,
    val currentYearMonth: YearMonth = YearMonth(year = today.year, month = today.monthValue),
    val schedules: List<ScheduleUiModel> = emptyList(),
    val scheduleBottomSheetContent: ScheduleBottomSheetContent? = null,
    val isScheduleBottomSheetVisible: Boolean = false,
    val isLoginDialogVisible: Boolean = false,
) : UiState {
    val baseToday: LocalDate = today
    val selectedDateSchedules: List<ScheduleUiModel>
        get() =
            schedules
                .filter { schedule ->
                    when (schedule) {
                        is AcademicScheduleUiModel -> schedule.endAt == selectedDate
                        is PersonalScheduleUiModel -> schedule.endAt.toLocalDate() == selectedDate
                    }
                }.sortedWith(
                    compareBy(
                        {
                            when (it) {
                                is PersonalScheduleUiModel -> 0
                                is AcademicScheduleUiModel -> 1
                            }
                        },
                        { if (it is PersonalScheduleUiModel) it.isCompleted else false },
                        {
                            when (it) {
                                is AcademicScheduleUiModel -> it.endAt.atStartOfDay()
                                is PersonalScheduleUiModel -> it.endAt
                            }
                        },
                        {
                            when (it) {
                                is AcademicScheduleUiModel -> it.startAt.atStartOfDay()
                                else -> null
                            }
                        },
                    ),
                )
}
