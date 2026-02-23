package pusan.university.plato_calendar.presentation.common.component.dialog.plato

import androidx.compose.runtime.Composable
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.LoginDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.NotificationPermissionDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.PlatoDialogContent
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.PlatoDialogContent.LoginContent
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.PlatoDialogContent.NotificationPermissionContent
import pusan.university.plato_calendar.presentation.main.intent.MainEvent
import pusan.university.plato_calendar.presentation.main.intent.MainState

@Composable
fun PlatoDialog(
    content: PlatoDialogContent?,
    state: MainState,
    onEvent: (MainEvent) -> Unit,
) {
    content?.let {
        when (it) {
            NotificationPermissionContent -> {
                NotificationPermissionDialog(
                    onDismiss = { onEvent(MainEvent.HideDialog) },
                    onConfirm = { onEvent(MainEvent.ConfirmNotificationPermission) },
                )
            }

            LoginContent -> {
                LoginDialog(
                    isLoggingIn = state.isLoggingIn,
                    onDismiss = { onEvent(MainEvent.HideDialog) },
                    onConfirm = { credentials ->
                        onEvent(MainEvent.ConfirmLogin(credentials))
                    },
                )
            }
        }
    }
}
