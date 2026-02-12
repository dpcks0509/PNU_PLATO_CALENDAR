package pusan.university.plato_calendar.presentation.common.component.dialog.content

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialSelectedDateMillis: Long,
    minDateMillis: Long,
    maxDateMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (selectedDateMillis: Long) -> Unit,
) {
    val zoneId = ZoneId.systemDefault()
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = Instant.ofEpochMilli(utcTimeMillis).atZone(zoneId).toLocalDate()
            val minDate = Instant.ofEpochMilli(minDateMillis).atZone(zoneId).toLocalDate()
            val maxDate = Instant.ofEpochMilli(maxDateMillis).atZone(zoneId).toLocalDate()
            return !date.isBefore(minDate) && !date.isAfter(maxDate)
        }

        override fun isSelectableYear(year: Int): Boolean {
            val minYear = Instant.ofEpochMilli(minDateMillis).atZone(zoneId).year
            val maxYear = Instant.ofEpochMilli(maxDateMillis).atZone(zoneId).year
            return year in minYear..maxYear
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        selectableDates = selectableDates,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        onConfirm(millis)
                    }
                    onDismiss()
                },
            ) { Text(text = "확인") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "취소") }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}
