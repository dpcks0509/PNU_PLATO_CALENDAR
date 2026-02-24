package pusan.university.plato_calendar.presentation.main.intent

import pusan.university.plato_calendar.presentation.util.base.UiSideEffect

sealed interface MainSideEffect : UiSideEffect {
    data object NavigateToNotificationSettings : MainSideEffect

    data object NavigateToCalendar: MainSideEffect
}
