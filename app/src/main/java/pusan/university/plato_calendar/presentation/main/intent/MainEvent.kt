package pusan.university.plato_calendar.presentation.main.intent

import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.presentation.common.base.UiEvent
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.PlatoDialogContent

sealed interface MainEvent : UiEvent {
    data class ShowDialog(val content: PlatoDialogContent) : MainEvent

    data object HideDialog : MainEvent

    data object ConfirmNotificationPermission : MainEvent

    data class ConfirmLogin(val credentials: LoginCredentials) : MainEvent

    data object NavigateToCalendar : MainEvent
}
