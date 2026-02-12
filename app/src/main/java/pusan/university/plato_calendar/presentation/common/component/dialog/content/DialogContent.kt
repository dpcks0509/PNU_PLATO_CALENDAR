package pusan.university.plato_calendar.presentation.common.component.dialog.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pusan.university.plato_calendar.domain.entity.LoginCredentials

@Parcelize
sealed interface DialogContent : Parcelable {
    data class DeleteSchedule(
        val scheduleId: Long,
        val onConfirm: () -> Unit,
    ) : DialogContent

    data class NotificationPermission(
        val onConfirm: () -> Unit,
    ) : DialogContent

    data class Login(
        val onConfirm: suspend (LoginCredentials) -> Unit,
    ) : DialogContent

    data class TimePicker(
        val initialHour: Int,
        val initialMinute: Int,
        val onConfirm: (hour: Int, minute: Int) -> Unit,
    ) : DialogContent

    data class DatePicker(
        val initialSelectedDateMillis: Long,
        val minDateMillis: Long,
        val maxDateMillis: Long,
        val onConfirm: (selectedDateMillis: Long) -> Unit,
    ) : DialogContent
}
