package pusan.university.plato_calendar.presentation.common.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pusan.university.plato_calendar.presentation.common.component.dialog.content.DialogContent

object DialogEventBus {
    private val _events = MutableSharedFlow<DialogEvent>()
    val events: SharedFlow<DialogEvent> = _events.asSharedFlow()

    suspend fun show(content: DialogContent) {
        _events.emit(DialogEvent.Show(content))
    }
}

sealed interface DialogEvent {
    data class Show(val content: DialogContent) : DialogEvent
    data object Dismiss : DialogEvent
}
