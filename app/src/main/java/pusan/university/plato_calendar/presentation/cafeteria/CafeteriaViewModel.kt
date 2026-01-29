package pusan.university.plato_calendar.presentation.cafeteria

import dagger.hilt.android.lifecycle.HiltViewModel
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaSideEffect
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class CafeteriaViewModel
    @Inject
    constructor() :
    BaseViewModel<CafeteriaState, CafeteriaEvent, CafeteriaSideEffect>(
            initialState = CafeteriaState(),
        ) {
        override suspend fun handleEvent(event: CafeteriaEvent) {
            // TODO: Handle events
        }
    }
