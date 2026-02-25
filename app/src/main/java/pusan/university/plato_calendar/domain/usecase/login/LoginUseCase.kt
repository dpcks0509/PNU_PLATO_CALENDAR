package pusan.university.plato_calendar.domain.usecase.login

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.entity.LoginSession
import pusan.university.plato_calendar.domain.exception.InvalidCredentialsException
import pusan.university.plato_calendar.domain.repository.LoginCredentialsRepository
import pusan.university.plato_calendar.domain.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor(
        private val loginRepository: LoginRepository,
        private val loginCredentialsRepository: LoginCredentialsRepository,
    ) {
        suspend operator fun invoke(credentials: LoginCredentials): ApiResult<LoginSession> {
            val result = loginRepository.login(credentials)
            when {
                result is ApiResult.Success -> loginCredentialsRepository.saveLoginCredentials(credentials)
                result is ApiResult.Error && result.exception is InvalidCredentialsException -> loginCredentialsRepository.deleteLoginCredentials()
            }
            return result
        }
    }
