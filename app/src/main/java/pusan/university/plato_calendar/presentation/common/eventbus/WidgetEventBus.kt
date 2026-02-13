package pusan.university.plato_calendar.presentation.common.eventbus

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object WidgetEventBus {
    private val _events = MutableSharedFlow<WidgetEvent>(replay = 1)
    val events: SharedFlow<WidgetEvent> = _events.asSharedFlow()

    suspend fun sendEvent(event: WidgetEvent) {
        _events.emit(event)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun consumeEvent() {
        _events.resetReplayCache()
    }
}

sealed interface WidgetEvent {
    data class OpenSchedule(val scheduleId: Long, val date: String? = null) : WidgetEvent

    data class OpenNewSchedule(val date: String? = null) : WidgetEvent
}
