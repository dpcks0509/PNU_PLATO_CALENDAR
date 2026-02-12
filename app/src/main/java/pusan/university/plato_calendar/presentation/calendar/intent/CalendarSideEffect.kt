package pusan.university.plato_calendar.presentation.calendar.intent

import pusan.university.plato_calendar.presentation.common.base.UiSideEffect

sealed interface CalendarSideEffect : UiSideEffect {
    data object HideScheduleBottomSheet : CalendarSideEffect

    data object ShowScheduleBottomSheet : CalendarSideEffect

    data class ScrollToPage(val page: Int) : CalendarSideEffect

    data object ShowLoginDialog : CalendarSideEffect
}
