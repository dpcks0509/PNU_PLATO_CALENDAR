package pusan.university.plato_calendar.domain.usecase.login

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.repository.LoginCredentialsRepository
import javax.inject.Inject

class GetLoginCredentialsUseCase
    @Inject
    constructor(
        private val loginCredentialsRepository: LoginCredentialsRepository,
    ) {
        operator fun invoke(): Flow<LoginCredentials?> =
            loginCredentialsRepository.getLoginCredentials()
    }
