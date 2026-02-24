package pusan.university.plato_calendar.presentation.setting.intent

import pusan.university.plato_calendar.presentation.util.base.UiSideEffect

sealed interface SettingSideEffect : UiSideEffect {
    data class NavigateToWebView(
        val url: String,
    ) : SettingSideEffect
}
