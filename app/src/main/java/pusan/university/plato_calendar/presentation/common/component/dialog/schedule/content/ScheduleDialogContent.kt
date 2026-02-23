package pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content

sealed interface ScheduleDialogContent {
    data class DeleteScheduleContent(
        val scheduleId: Long,
        val onConfirm: () -> Unit,
    ) : ScheduleDialogContent

    data class DatePickerContent(
        val initialSelectedDateMillis: Long,
        val minDateMillis: Long,
        val maxDateMillis: Long,
        val onConfirm: (selectedDateMillis: Long) -> Unit,
    ) : ScheduleDialogContent

    data class TimePickerContent(
        val initialHour: Int,
        val initialMinute: Int,
        val onConfirm: (hour: Int, minute: Int) -> Unit,
    ) : ScheduleDialogContent
}
