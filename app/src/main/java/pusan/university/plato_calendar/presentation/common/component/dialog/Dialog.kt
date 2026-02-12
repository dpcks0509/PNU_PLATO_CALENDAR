package pusan.university.plato_calendar.presentation.common.component.dialog

import androidx.compose.runtime.Composable
import pusan.university.plato_calendar.presentation.common.component.dialog.content.DatePickerDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.content.DeleteScheduleDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.content.DialogContent
import pusan.university.plato_calendar.presentation.common.component.dialog.content.LoginDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.content.NotificationPermissionDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.content.TimePickerDialog

@Composable
fun Dialog(state: DialogState) {
    state.content?.let { content ->
        when (content) {
            is DialogContent.DeleteScheduleContent -> {
                DeleteScheduleDialog(
                    onDismiss = { state.hide() },
                    onConfirm = {
                        state.hide()
                        content.onConfirm()
                    },
                )
            }

            is DialogContent.NotificationPermissionContent -> {
                NotificationPermissionDialog(
                    onDismiss = { state.hide() },
                    onConfirm = {
                        state.hide()
                        content.onConfirm()
                    },
                )
            }

            is DialogContent.LoginContent -> {
                LoginDialog(
                    onDismiss = { state.hide() },
                    onConfirm = { credentials ->
                        content.onConfirm(credentials)
                        state.hide()
                    },
                )
            }

            is DialogContent.DatePickerContent -> {
                DatePickerDialog(
                    initialSelectedDateMillis = content.initialSelectedDateMillis,
                    minDateMillis = content.minDateMillis,
                    maxDateMillis = content.maxDateMillis,
                    onDismiss = { state.hide() },
                    onConfirm = { selectedDateMillis ->
                        state.hide()
                        content.onConfirm(selectedDateMillis)
                    },
                )
            }

            is DialogContent.TimePickerContent -> {
                TimePickerDialog(
                    initialHour = content.initialHour,
                    initialMinute = content.initialMinute,
                    onDismiss = { state.hide() },
                    onConfirm = { hour, minute ->
                        state.hide()
                        content.onConfirm(hour, minute)
                    },
                )
            }
        }
    }
}
