package pusan.university.plato_calendar.domain.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.LoginCredentials

interface LoginCredentialsRepository {
    fun getLoginCredentials(): Flow<LoginCredentials?>

    suspend fun saveLoginCredentials(credentials: LoginCredentials)

    suspend fun deleteLoginCredentials()
}
