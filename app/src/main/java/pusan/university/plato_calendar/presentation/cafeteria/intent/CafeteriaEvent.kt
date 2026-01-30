package pusan.university.plato_calendar.presentation.cafeteria.intent

import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.presentation.common.base.UiEvent

sealed interface CafeteriaEvent : UiEvent {
    data class PinCafeteria(val cafeteria: Cafeteria): CafeteriaEvent
    object UnPinCafeteria: CafeteriaEvent
}
