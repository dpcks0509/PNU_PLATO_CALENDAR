package pusan.university.plato_calendar.data.local.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.data.local.database.LoginCredentialsDataStore
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.repository.LoginCredentialsRepository
import javax.inject.Inject

class LocalLoginCredentialsRepository
    @Inject
    constructor(
        private val loginCredentialsDataStore: LoginCredentialsDataStore,
    ) : LoginCredentialsRepository {
        override fun getLoginCredentials(): Flow<LoginCredentials?> =
            loginCredentialsDataStore.loginCredentials

        override suspend fun saveLoginCredentials(credentials: LoginCredentials) {
            loginCredentialsDataStore.saveLoginCredentials(credentials)
        }

        override suspend fun deleteLoginCredentials() {
            loginCredentialsDataStore.deleteLoginCredentials()
        }
    }
