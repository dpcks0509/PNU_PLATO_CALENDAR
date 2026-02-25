package pusan.university.plato_calendar.domain.usecase.login

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.repository.LoginCredentialsRepository
import pusan.university.plato_calendar.domain.repository.LoginRepository
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val loginRepository: LoginRepository,
        private val loginCredentialsRepository: LoginCredentialsRepository,
    ) {
        suspend operator fun invoke(sessKey: String): ApiResult<Unit> {
            val result = loginRepository.logout(sessKey)
            if (result is ApiResult.Success) {
                loginCredentialsRepository.deleteLoginCredentials()
            }
            return result
        }
    }
