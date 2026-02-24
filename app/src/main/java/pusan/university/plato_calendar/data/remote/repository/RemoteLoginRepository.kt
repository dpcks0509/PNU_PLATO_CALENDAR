package pusan.university.plato_calendar.data.remote.repository

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import pusan.university.plato_calendar.data.local.database.LoginCredentialsDataStore
import pusan.university.plato_calendar.data.remote.service.LoginService
import pusan.university.plato_calendar.data.util.ApiResponse
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.data.util.handleApiResponse
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
        override suspend fun login(credentials: LoginCredentials): ApiResult<LoginSession> {
            val response =
                handleApiResponse {
                    loginService.login(
                        userName = credentials.userName,
                        password = credentials.password,
                    )
                }

            if (response is ApiResponse.Failure.NetworkException) return ApiResult.Error(response.exception)
            if (response is ApiResponse.Failure.UnknownException) return ApiResult.Error(response.exception)
            if (response !is ApiResponse.Failure.HttpException || response.code != REDIRECT_CODE) {
                return ApiResult.Error(Exception(LOGIN_FAILED_ERROR))
            }

            val redirectLocation =
                response.headers["Location"]
                    ?: return ApiResult.Error(Exception(LOGIN_FAILED_ERROR))
            val redirectUrl = redirectLocation.toHttpUrlOrNull()

            when (redirectUrl?.queryParameter("errorcode")) {
                "1" -> {
                    return ApiResult.Error(Exception(COOKIES_DISABLED_ERROR))
                }

                "2" -> {
                    return ApiResult.Error(Exception(INVALID_USERNAME_FORMAT_ERROR))
                }

                "3" -> {
                    loginCredentialsDataStore.deleteLoginCredentials()
                    return ApiResult.Error(Exception(INVALID_CREDENTIALS_ERROR))
                }

                "4" -> {
                    return ApiResult.Error(Exception(SESSION_EXPIRED_ERROR))
                }

                "5" -> {
                    return ApiResult.Error(Exception(ACCOUNT_LOCKED_ERROR))
                }

                null -> {
                    Unit
                }

                else -> {
                    return ApiResult.Error(Exception(LOGIN_FAILED_ERROR))
                }
            }

            val userId =
                redirectUrl?.queryParameter("testsession")
                    ?: return ApiResult.Error(Exception(LOGIN_FAILED_ERROR))

            val redirectResponse = handleApiResponse { loginService.redirect() }
            if (redirectResponse is ApiResponse.Failure.NetworkException) {
                return ApiResult.Error(
                    redirectResponse.exception,
                )
            }
            if (redirectResponse is ApiResponse.Failure.UnknownException) {
                return ApiResult.Error(
                    redirectResponse.exception,
                )
            }
            if (redirectResponse !is ApiResponse.Success) {
                return ApiResult.Error(
                    Exception(
                        LOGIN_FAILED_ERROR,
                    ),
                )
            }

            val redirectResponseBody =
                redirectResponse.data?.string()
                    ?: return ApiResult.Error(Exception(LOGIN_FAILED_ERROR))

            val sessKey =
                Regex(
                    pattern = """M\.cfg\s*=\s*\{[\s\S]*?"sesskey"\s*:\s*"([^"]+)""",
                ).find(redirectResponseBody)?.groupValues?.getOrNull(1)
                    ?: return ApiResult.Error(Exception(LOGIN_FAILED_ERROR))

            val fullName =
                Regex(
                    pattern = """class="fullname"[^>]*title="([^"]+)""",
                ).find(redirectResponseBody)?.groupValues?.getOrNull(1)
                    ?: return ApiResult.Error(Exception(LOGIN_FAILED_ERROR))

            return ApiResult.Success(
                LoginSession(
                    userName = credentials.userName,
                    fullName = fullName,
                    userId = userId,
                    sessKey = sessKey,
                ),
            )
        }

        override suspend fun logout(sessKey: String): ApiResult<Unit> {
            val response = handleApiResponse { loginService.logout(sessKey = sessKey) }

            if (response is ApiResponse.Failure.NetworkException) return ApiResult.Error(response.exception)
            if (response is ApiResponse.Failure.UnknownException) return ApiResult.Error(response.exception)
            if (response is ApiResponse.Failure.HttpException && response.code == REDIRECT_CODE) {
                val redirectLocation =
                    response.headers["Location"]
                        ?: return ApiResult.Error(Exception(LOGOUT_FAILED_ERROR))
                val redirectUrl = redirectLocation.toHttpUrlOrNull()

                when (redirectUrl?.queryParameter("errorcode")) {
                    null -> {
                        Unit
                    }

                    else -> {
                        return ApiResult.Error(Exception(LOGOUT_FAILED_ERROR))
                    }
                }
            }

            val redirectResponse = handleApiResponse { loginService.redirect() }
            if (redirectResponse is ApiResponse.Failure.NetworkException) {
                return ApiResult.Error(
                    redirectResponse.exception,
                )
            }
            if (redirectResponse is ApiResponse.Failure.UnknownException) {
                return ApiResult.Error(
                    redirectResponse.exception,
                )
            }
            if (redirectResponse !is ApiResponse.Success) {
                return ApiResult.Error(
                    Exception(
                        LOGOUT_FAILED_ERROR,
                    ),
                )
            }

            return ApiResult.Success(Unit)
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
