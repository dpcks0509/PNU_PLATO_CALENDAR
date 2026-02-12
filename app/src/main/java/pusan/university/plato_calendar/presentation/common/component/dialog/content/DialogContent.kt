package pusan.university.plato_calendar.presentation.common.component.dialog.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pusan.university.plato_calendar.domain.entity.LoginCredentials

@Parcelize
sealed interface DialogContent : Parcelable {
    data class DeleteScheduleContent(
        val scheduleId: Long,
        val onConfirm: () -> Unit,
    ) : DialogContent

    data class NotificationPermissionContent(
        val onConfirm: () -> Unit,
    ) : DialogContent

    data class LoginContent(
        val onConfirm: suspend (LoginCredentials) -> Unit,
    ) : DialogContent

    data class DatePickerContent(
        val initialSelectedDateMillis: Long,
        val minDateMillis: Long,
        val maxDateMillis: Long,
        val onConfirm: (selectedDateMillis: Long) -> Unit,
    ) : DialogContent

    data class TimePickerContent(
        val initialHour: Int,
        val initialMinute: Int,
        val onConfirm: (hour: Int, minute: Int) -> Unit,
    ) : DialogContent
}
