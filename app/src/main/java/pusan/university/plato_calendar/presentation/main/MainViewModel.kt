package pusan.university.plato_calendar.presentation.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.main.intent.MainEvent
import pusan.university.plato_calendar.presentation.main.intent.MainEvent.ConfirmLogin
import pusan.university.plato_calendar.presentation.main.intent.MainEvent.ConfirmNotificationPermission
import pusan.university.plato_calendar.presentation.main.intent.MainEvent.HideDialog
import pusan.university.plato_calendar.presentation.main.intent.MainEvent.NavigateToCalendar
import pusan.university.plato_calendar.presentation.main.intent.MainEvent.ShowDialog
import pusan.university.plato_calendar.presentation.main.intent.MainSideEffect
import pusan.university.plato_calendar.presentation.main.intent.MainState
import pusan.university.plato_calendar.presentation.util.base.BaseViewModel
import pusan.university.plato_calendar.presentation.util.eventbus.DialogEvent
import pusan.university.plato_calendar.presentation.util.eventbus.DialogEventBus
import pusan.university.plato_calendar.presentation.util.manager.LoginManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val loginManager: LoginManager,
) : BaseViewModel<MainState, MainEvent, MainSideEffect>(MainState()) {
    init {
        viewModelScope.launch {
            DialogEventBus.events.collect { event ->
                when (event) {
                    is DialogEvent.Show -> setState { copy(dialogContent = event.content) }
                    is DialogEvent.Dismiss -> setState { copy(dialogContent = null) }
                }
            }
        }
    }

    override suspend fun handleEvent(event: MainEvent) {
        when (event) {
            is ShowDialog -> setState { copy(dialogContent = event.content) }

            HideDialog -> setState { copy(dialogContent = null) }

            ConfirmNotificationPermission -> {
                setState { copy(dialogContent = null) }
                setSideEffect { MainSideEffect.NavigateToNotificationSettings }
            }

            is ConfirmLogin -> {
                setState { copy(isLoggingIn = true) }
                loginManager.login(event.credentials)
                setState { copy(isLoggingIn = false, dialogContent = null) }
            }

            NavigateToCalendar -> setSideEffect { MainSideEffect.NavigateToCalendar }
        }
    }
}
