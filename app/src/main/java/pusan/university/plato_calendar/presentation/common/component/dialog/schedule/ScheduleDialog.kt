package pusan.university.plato_calendar.presentation.common.component.dialog.schedule

import androidx.compose.runtime.Composable
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.DatePickerDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.DeleteScheduleDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.ScheduleDialogContent
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.ScheduleDialogContent.DatePickerContent
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.ScheduleDialogContent.DeleteScheduleContent
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.ScheduleDialogContent.TimePickerContent
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.TimePickerDialog

@Composable
fun ScheduleDialog(content: ScheduleDialogContent?, onDismiss: () -> Unit) {
    content?.let { content ->
        when (content) {
            is DeleteScheduleContent -> {
                DeleteScheduleDialog(
                    onDismiss = onDismiss,
                    onConfirm = {
                        onDismiss()
                        content.onConfirm()
                    },
                )
            }

            is DatePickerContent -> {
                DatePickerDialog(
                    initialSelectedDateMillis = content.initialSelectedDateMillis,
                    minDateMillis = content.minDateMillis,
                    maxDateMillis = content.maxDateMillis,
                    onDismiss = onDismiss,
                    onConfirm = { selectedDateMillis ->
                        onDismiss()
                        content.onConfirm(selectedDateMillis)
                    },
                )
            }

            is TimePickerContent -> {
                TimePickerDialog(
                    initialHour = content.initialHour,
                    initialMinute = content.initialMinute,
                    onDismiss = onDismiss,
                    onConfirm = { hour, minute ->
                        onDismiss()
                        content.onConfirm(hour, minute)
                    },
                )
            }
        }
    }
}
