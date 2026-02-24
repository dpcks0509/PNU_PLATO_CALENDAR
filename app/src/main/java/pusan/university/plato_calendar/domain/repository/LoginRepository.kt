package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.entity.LoginSession

interface LoginRepository {
    suspend fun login(credentials: LoginCredentials): ApiResult<LoginSession>
    suspend fun logout(sessKey: String): ApiResult<Unit>
}
