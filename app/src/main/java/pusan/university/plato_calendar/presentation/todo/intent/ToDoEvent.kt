package pusan.university.plato_calendar.presentation.todo.intent

import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.common.base.UiEvent
import pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content.ScheduleDialogContent

sealed interface ToDoEvent : UiEvent {
    data object Refresh : ToDoEvent

    data class TogglePersonalScheduleCompletion(
        val id: Long,
        val isCompleted: Boolean,
    ) : ToDoEvent

    data class ShowScheduleBottomSheet(
        val schedule: ScheduleUiModel? = null,
    ) : ToDoEvent

    data class EditCustomSchedule(
        val customSchedule: CustomSchedule,
    ) : ToDoEvent

    data class DeleteCustomSchedule(
        val id: Long,
    ) : ToDoEvent

    data class ShowDialog(
        val content: ScheduleDialogContent,
    ) : ToDoEvent

    data object HideDialog : ToDoEvent
}
