package pusan.university.plato_calendar.presentation.common.component.dialog.plato

import androidx.compose.runtime.Composable
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.LoginDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.NotificationPermissionDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.PlatoDialogContent

@Composable
fun PlatoDialog(state: PlatoDialogState) {
    state.content?.let { content ->
        when (content) {
            is PlatoDialogContent.NotificationPermissionContent -> {
                NotificationPermissionDialog(
                    onDismiss = { state.hide() },
                    onConfirm = {
                        state.hide()
                        content.onConfirm()
                    },
                )
            }

            is PlatoDialogContent.LoginContent -> {
                LoginDialog(
                    onDismiss = { state.hide() },
                    onConfirm = { credentials ->
                        content.onConfirm(credentials)
                        state.hide()
                    },
                )
            }
        }
    }
}
