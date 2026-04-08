package pusan.university.plato_calendar.presentation.cafeteria.intent

import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab
import pusan.university.plato_calendar.presentation.util.base.UiEvent

sealed interface CafeteriaEvent : UiEvent {
    data class SelectCafeteria(val cafeteria: Cafeteria) : CafeteriaEvent
    data class SelectDormitory(val dormitory: Dormitory) : CafeteriaEvent
    data class SelectTab(val tab: CafeteriaTab) : CafeteriaEvent
    data object PreviousDay : CafeteriaEvent
    data object NextDay : CafeteriaEvent
    data object Refresh : CafeteriaEvent
}
