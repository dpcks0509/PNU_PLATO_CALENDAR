package pusan.university.plato_calendar.presentation.common.component.dialog.plato.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pusan.university.plato_calendar.domain.entity.LoginCredentials

@Parcelize
sealed interface PlatoDialogContent : Parcelable {
    data class NotificationPermissionContent(
        val onConfirm: () -> Unit,
    ) : PlatoDialogContent

    data class LoginContent(
        val onConfirm: suspend (LoginCredentials) -> Unit,
    ) : PlatoDialogContent
}
