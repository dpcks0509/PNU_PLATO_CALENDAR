package pusan.university.plato_calendar.presentation.todo.intent

import pusan.university.plato_calendar.presentation.util.base.UiSideEffect

sealed interface ToDoSideEffect : UiSideEffect {
    data object HideScheduleBottomSheet : ToDoSideEffect

    data object ShowScheduleBottomSheet : ToDoSideEffect
}
