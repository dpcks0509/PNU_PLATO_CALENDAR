package pusan.university.plato_calendar.presentation.common.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import pusan.university.plato_calendar.presentation.common.component.dialog.content.DialogContent
import pusan.university.plato_calendar.presentation.common.eventbus.DialogEvent
import pusan.university.plato_calendar.presentation.common.eventbus.DialogEventBus

class DialogState {
    var content by mutableStateOf<DialogContent?>(null)
        private set

    fun show(content: DialogContent) {
        this.content = content
    }

    fun hide() {
        content = null
    }

    companion object {
        private val DialogContentSaver = Saver<DialogState, DialogContent>(
            save = { it.content },
            restore = { savedContent ->
                DialogState().apply { show(savedContent) }
            }
        )

        @Composable
        fun rememberDialogState(): DialogState {
            val state = rememberSaveable(saver = DialogContentSaver) { DialogState() }

            LaunchedEffect(Unit) {
                DialogEventBus.events.collect { event ->
                    when (event) {
                        is DialogEvent.Show -> state.show(event.content)
                        is DialogEvent.Dismiss -> state.hide()
                    }
                }
            }

            return state
        }
    }
}

