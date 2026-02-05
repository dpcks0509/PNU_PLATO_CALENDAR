package pusan.university.plato_calendar.data.remote.repository

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import pusan.university.plato_calendar.data.local.database.LoginCredentialsDataStore
import pusan.university.plato_calendar.data.remote.service.LoginService
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.entity.LoginSession
import pusan.university.plato_calendar.domain.repository.LoginRepository
import javax.inject.Inject

class RemoteLoginRepository
@Inject
constructor(
    private val loginService: LoginService,
    private val loginCredentialsDataStore: LoginCredentialsDataStore,
) : LoginRepository {
    override suspend fun login(credentials: LoginCredentials): Result<LoginSession> {
        return try {
            val response =
                loginService.login(
                    userName = credentials.userName,
                    password = credentials.password,
                )

            if (response.code() == REDIRECT_CODE) {
                val redirectLocation =
                    response.headers()["Location"]
                        ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))
                val redirectUrl = redirectLocation.toHttpUrlOrNull()

                when (redirectUrl?.queryParameter("errorcode")) {
                    "1" -> return Result.failure(Exception(COOKIES_DISABLED_ERROR))
                    "2" -> return Result.failure(Exception(INVALID_USERNAME_FORMAT_ERROR))
                    "3" -> {
                        loginCredentialsDataStore.deleteLoginCredentials()
                        return Result.failure(Exception(INVALID_CREDENTIALS_ERROR))
                    }

                    "4" -> return Result.failure(Exception(SESSION_EXPIRED_ERROR))
                    "5" -> return Result.failure(Exception(ACCOUNT_LOCKED_ERROR))
                    null -> Unit
                    else -> return Result.failure(Exception(LOGIN_FAILED_ERROR))
                }

                val userId =
                    redirectUrl?.queryParameter("testsession")
                        ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))

                val redirectResponse = loginService.redirect()

                if (redirectResponse.isSuccessful) {
                    val redirectResponseBody =
                        redirectResponse.body()?.string() ?: return Result.failure(
                            Exception(LOGIN_FAILED_ERROR),
                        )

                    val sessKey =
                        Regex(
                            pattern = """M\.cfg\s*=\s*\{[\s\S]*?"sesskey"\s*:\s*"([^"]+)""",
                        ).find(redirectResponseBody)?.groupValues?.getOrNull(1)
                            ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))

                    val fullName =
                        Regex(
                            pattern = """class="fullname"[^>]*title="([^"]+)""",
                        ).find(redirectResponseBody)?.groupValues?.getOrNull(1)
                            ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))

                    return Result.success(
                        LoginSession(
                            userName = credentials.userName,
                            fullName = fullName,
                            userId = userId,
                            sessKey = sessKey,
                        ),
                    )
                }
            }

            Result.failure(Exception(LOGIN_FAILED_ERROR))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(sessKey: String): Result<Unit> {
        return try {
            val response = loginService.logout(sessKey = sessKey)

            if (response.code() == REDIRECT_CODE) {
                val redirectLocation =
                    response.headers()["Location"]
                        ?: return Result.failure(Exception(LOGOUT_FAILED_ERROR))
                val redirectUrl = redirectLocation.toHttpUrlOrNull()

                when (redirectUrl?.queryParameter("errorcode")) {
                    null -> Unit
                    else -> return Result.failure(Exception(LOGOUT_FAILED_ERROR))
                }
            }

            val redirectResponse = loginService.redirect()

            if (redirectResponse.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(LOGOUT_FAILED_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val REDIRECT_CODE = 303
        private const val COOKIES_DISABLED_ERROR = "현재, 브라우저의 쿠키가 작동하지 않습니다."
        private const val INVALID_USERNAME_FORMAT_ERROR =
            "사용자 아이디: 이이디에는 영어소문자, 숫자, 밑줄( _ ), 하이폰( - ), 마침표( . ) 또는 @ 기호만을 쓸 수 있습니다."
        private const val INVALID_CREDENTIALS_ERROR = "아이디 또는 패스워드가 잘못 입력되었습니다."
        private const val SESSION_EXPIRED_ERROR = "세션이 종료 되었습니다. 다시 로그인 하십시오."
        private const val ACCOUNT_LOCKED_ERROR =
            "로그인 시도 5회 실패로 인해 계정이 일시적으로 잠겼습니다.\n" +
                    "30분 후 다시 시도해 주세요."
        private const val LOGIN_FAILED_ERROR = "로그인에 실패했습니다."
        private const val LOGOUT_FAILED_ERROR = "로그아웃에 실패했습니다."
    }
}
