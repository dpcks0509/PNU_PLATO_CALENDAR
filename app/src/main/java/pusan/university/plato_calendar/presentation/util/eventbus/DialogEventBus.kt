package pusan.university.plato_calendar.presentation.util.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pusan.university.plato_calendar.presentation.util.component.dialog.plato.content.PlatoDialogContent

object DialogEventBus {
    private val _events = MutableSharedFlow<DialogEvent>()
    val events: SharedFlow<DialogEvent> = _events.asSharedFlow()

    suspend fun show(content: PlatoDialogContent) {
        _events.emit(DialogEvent.Show(content))
    }
}

sealed interface DialogEvent {
    data class Show(val content: PlatoDialogContent) : DialogEvent
    data object Dismiss : DialogEvent
}
