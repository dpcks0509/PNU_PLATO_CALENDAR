package pusan.university.plato_calendar.presentation.setting

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.presentation.common.base.BaseViewModel
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.manager.SettingsManager
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent.HideLoginDialog
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent.ShowLoginDialog
import pusan.university.plato_calendar.presentation.setting.intent.SettingSideEffect
import pusan.university.plato_calendar.presentation.setting.intent.SettingState
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
@Inject
constructor(
    private val loginManager: LoginManager,
    private val settingsManager: SettingsManager,
) : BaseViewModel<SettingState, SettingEvent, SettingSideEffect>(SettingState()) {
    init {
        viewModelScope.launch {
            loginManager.loginStatus.collect { loginStatus ->
                when (loginStatus) {
                    is LoginStatus.Login -> {
                        setState { copy(userInfo = loginStatus.loginSession.userInfo) }
                    }

                    else -> setState { copy(userInfo = null) }
                }
            }
        }

        viewModelScope.launch {
            settingsManager.appSettings.collect { appSettings ->
                setState {
                    copy(
                        notificationsEnabled = appSettings.notificationsEnabled,
                        firstReminderTime = appSettings.firstReminderTime,
                        secondReminderTime = appSettings.secondReminderTime,
                    )
                }
            }
        }
    }

    override suspend fun handleEvent(event: SettingEvent) {
        when (event) {
            ShowLoginDialog -> setState { copy(isLoginDialogVisible = true) }
            HideLoginDialog -> setState { copy(isLoginDialogVisible = false) }
            SettingEvent.Logout -> {
                loginManager.logout()
            }

            is SettingEvent.UpdateNotificationsEnabled -> {
                settingsManager.setNotificationsEnabled(event.enabled)
            }

            is SettingEvent.UpdateFirstReminderTime -> {
                val currentSecondReminderTime = state.value.secondReminderTime
                val desiredFirstReminderTime = event.time

                val (normalizedFirstReminderTime, normalizedSecondReminderTime) =
                    normalizeReminderTimes(
                        firstReminderCandidate = desiredFirstReminderTime,
                        secondReminderCandidate = currentSecondReminderTime,
                    )

                updateReminderTimes(
                    updatedFirstReminderTime = normalizedFirstReminderTime,
                    updatedSecondReminderTime = normalizedSecondReminderTime,
                )
            }

            is SettingEvent.UpdateSecondReminderTime -> {
                val currentFirstReminderTime = state.value.firstReminderTime
                val desiredSecondReminderTime = event.time

                val (normalizedFirstReminderTime, normalizedSecondReminderTime) =
                    normalizeReminderTimes(
                        firstReminderCandidate = currentFirstReminderTime,
                        secondReminderCandidate = desiredSecondReminderTime,
                    )

                updateReminderTimes(
                    updatedFirstReminderTime = normalizedFirstReminderTime,
                    updatedSecondReminderTime = normalizedSecondReminderTime,
                )
            }

            is SettingEvent.UpdateNotificationPermission -> {
                setState { copy(hasNotificationPermission = event.granted) }
            }

            is SettingEvent.NavigateToWebView -> {
                setSideEffect { SettingSideEffect.NavigateToWebView(event.url) }
            }
        }
    }

    suspend fun tryLogin(credentials: LoginCredentials): Boolean {
        val isLoginSuccess = loginManager.login(credentials)
        if (isLoginSuccess) setState { copy(isLoginDialogVisible = false) }

        return isLoginSuccess
    }

    private fun normalizeReminderTimes(
        firstReminderCandidate: NotificationTime,
        secondReminderCandidate: NotificationTime,
    ): Pair<NotificationTime, NotificationTime> {
        if (firstReminderCandidate == NotificationTime.NONE && secondReminderCandidate == NotificationTime.NONE) {
            return NotificationTime.NONE to NotificationTime.NONE
        }

        if (firstReminderCandidate == NotificationTime.NONE) {
            return secondReminderCandidate to NotificationTime.NONE
        }

        if (secondReminderCandidate == NotificationTime.NONE) {
            return firstReminderCandidate to NotificationTime.NONE
        }

        if (firstReminderCandidate == secondReminderCandidate) {
            return firstReminderCandidate to NotificationTime.NONE
        }

        return if (firstReminderCandidate.ordinal <= secondReminderCandidate.ordinal) {
            firstReminderCandidate to secondReminderCandidate
        } else {
            secondReminderCandidate to firstReminderCandidate
        }
    }

    private suspend fun updateReminderTimes(
        updatedFirstReminderTime: NotificationTime,
        updatedSecondReminderTime: NotificationTime,
    ) {
        settingsManager.setFirstReminderTime(updatedFirstReminderTime)
        settingsManager.setSecondReminderTime(updatedSecondReminderTime)

        setState {
            copy(
                firstReminderTime = updatedFirstReminderTime,
                secondReminderTime = updatedSecondReminderTime,
            )
        }
    }
}
