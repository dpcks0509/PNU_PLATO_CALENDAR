package pusan.university.plato_calendar.presentation.util.component.dialog.plato.content

sealed interface PlatoDialogContent {
    data object NotificationPermissionContent : PlatoDialogContent

    data object LoginContent : PlatoDialogContent

    data object WidgetRequiredContent : PlatoDialogContent

    data object WidgetRemovalRequiredContent : PlatoDialogContent
}
