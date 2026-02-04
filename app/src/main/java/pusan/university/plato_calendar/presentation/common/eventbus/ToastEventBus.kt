package pusan.university.plato_calendar.presentation.common.eventbus

import android.os.Parcelable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.parcelize.Parcelize

object ToastEventBus {
    private val _toastMessage = MutableSharedFlow<ToastMessage>(replay = 1)
    val toastMessage = _toastMessage.asSharedFlow()

    suspend fun sendSuccess(message: String?) {
        if (message.isNullOrEmpty()) return

        _toastMessage.emit(ToastMessage.Success(message))
    }

    suspend fun sendError(message: String?) {
        if (message.isNullOrEmpty()) return

        _toastMessage.emit(ToastMessage.Error(message))
    }
}

sealed class ToastMessage : Parcelable {
    abstract val message: String

    @Parcelize
    data class Success(
        override val message: String,
    ) : ToastMessage()

    @Parcelize
    data class Error(
        override val message: String,
    ) : ToastMessage()
}
