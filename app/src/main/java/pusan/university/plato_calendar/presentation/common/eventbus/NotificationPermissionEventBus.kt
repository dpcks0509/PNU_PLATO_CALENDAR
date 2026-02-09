package pusan.university.plato_calendar.presentation.common.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationPermissionEventBus {
    private val _events = MutableSharedFlow<NotificationPermissionEvent>()
    val events: SharedFlow<NotificationPermissionEvent> = _events.asSharedFlow()

    suspend fun sendEvent(event: NotificationPermissionEvent) {
        _events.emit(event)
    }
}

sealed interface NotificationPermissionEvent {
    data object RequestPermission : NotificationPermissionEvent
    data class PermissionResult(val isGranted: Boolean) : NotificationPermissionEvent
}
