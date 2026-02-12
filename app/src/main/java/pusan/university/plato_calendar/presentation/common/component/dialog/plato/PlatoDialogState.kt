package pusan.university.plato_calendar.presentation.common.component.dialog.plato

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.PlatoDialogContent

class PlatoDialogState {
    var content by mutableStateOf<PlatoDialogContent?>(null)
        private set

    fun show(content: PlatoDialogContent) {
        this.content = content
    }

    fun hide() {
        content = null
    }

    companion object Companion {
        private val PlatoDialogContentSaver = Saver<PlatoDialogState, PlatoDialogContent>(
            save = { it.content },
            restore = { savedContent ->
                PlatoDialogState().apply { show(savedContent) }
            }
        )

        @Composable
        fun rememberPlatoDialogState(): PlatoDialogState =
            rememberSaveable(saver = PlatoDialogContentSaver) { PlatoDialogState() }
    }
}