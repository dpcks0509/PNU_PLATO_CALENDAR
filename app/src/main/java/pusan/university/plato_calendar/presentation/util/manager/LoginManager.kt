package pusan.university.plato_calendar.presentation.util.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.usecase.login.GetLoginCredentialsUseCase
import pusan.university.plato_calendar.domain.usecase.login.LoginUseCase
import pusan.university.plato_calendar.domain.usecase.login.LogoutUseCase
import pusan.university.plato_calendar.presentation.util.eventbus.ToastEventBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginManager
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val getLoginCredentialsUseCase: GetLoginCredentialsUseCase,
    ) {
        private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.Uninitialized)
        val loginStatus: StateFlow<LoginStatus> = _loginStatus.asStateFlow()

        suspend fun autoLogin() {
            if (loginStatus.value is LoginStatus.LoginInProgress) return

            _loginStatus.update { LoginStatus.LoginInProgress }

            val loginCredentials = getLoginCredentialsUseCase().firstOrNull()

            if (loginCredentials != null) {
                when (val result = loginUseCase(loginCredentials)) {
                    is ApiResult.Success -> {
                        _loginStatus.update { LoginStatus.Login(result.data) }
                    }
                    is ApiResult.Error -> {
                        if (result.exception is NoNetworkConnectivityException) {
                            _loginStatus.update { LoginStatus.NetworkDisconnected }
                        } else {
                            _loginStatus.update { LoginStatus.Logout }
                            ToastEventBus.sendError(result.exception.message)
                        }
                    }
                }
            } else {
                _loginStatus.update { LoginStatus.Logout }
            }
        }

        suspend fun login(credentials: LoginCredentials) {
            if (loginStatus.value !is LoginStatus.Login) {
                when (val result = loginUseCase(credentials)) {
                    is ApiResult.Success -> {
                        _loginStatus.update { LoginStatus.Login(result.data) }
                        ToastEventBus.sendSuccess("로그인에 성공했습니다.")
                    }
                    is ApiResult.Error -> ToastEventBus.sendError(result.exception.message)
                }
            }
        }

        suspend fun logout() {
            val loginStatus = loginStatus.value

            if (loginStatus is LoginStatus.Login) {
                when (val result = logoutUseCase(loginStatus.loginSession.sessKey)) {
                    is ApiResult.Success -> {
                        _loginStatus.update { LoginStatus.Logout }
                        ToastEventBus.sendSuccess("로그아웃에 성공했습니다.")
                    }
                    is ApiResult.Error -> ToastEventBus.sendError(result.exception.message)
                }
            }
        }
    }
