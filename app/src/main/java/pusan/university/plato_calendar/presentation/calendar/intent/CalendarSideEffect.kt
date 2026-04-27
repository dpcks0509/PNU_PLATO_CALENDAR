package pusan.university.plato_calendar.presentation.calendar.intent

import pusan.university.plato_calendar.presentation.util.base.UiSideEffect
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent

sealed interface CalendarSideEffect : UiSideEffect {
    data object HideScheduleBottomSheet : CalendarSideEffect

    data class ShowScheduleBottomSheet(
        val content: ScheduleBottomSheetContent,
    ) : CalendarSideEffect

    data class ScrollToPage(val page: Int) : CalendarSideEffect

    data object ShowLoginDialog : CalendarSideEffect
}
