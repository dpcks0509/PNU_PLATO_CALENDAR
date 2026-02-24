package pusan.university.plato_calendar.presentation.main.intent

import pusan.university.plato_calendar.presentation.util.base.UiState
import pusan.university.plato_calendar.presentation.util.component.dialog.plato.content.PlatoDialogContent

data class MainState(
    val dialogContent: PlatoDialogContent? = null,
    val isLoggingIn: Boolean = false,
) : UiState
