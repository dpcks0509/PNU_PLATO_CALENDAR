package pusan.university.plato_calendar.data.util

import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ApiException(
    val code: Int,
    message: String?,
) : Exception(message)

sealed interface ApiResponse<out T> {
    data class Success<out T>(
        val data: T?,
    ) : ApiResponse<T>

    sealed interface Failure : ApiResponse<Nothing> {
        data class HttpException(
            val code: Int,
            val message: String?,
            val headers: Map<String, String> = emptyMap(),
        ) : Failure

        data class NetworkException(
            val exception: Throwable,
            val message: String? = null,
        ) : Failure

        data class UnknownException(
            val exception: Throwable,
            val message: String? = null,
        ) : Failure
    }
}

suspend inline fun <T> handleApiResponse(crossinline call: suspend () -> Response<T>): ApiResponse<T> =
    try {
        val response = call()
        if (response.isSuccessful) {
            ApiResponse.Success(response.body())
        } else {
            ApiResponse.Failure.HttpException(
                code = response.code(),
                message = response.errorBody()?.string(),
                headers =
                    response
                        .headers()
                        .names()
                        .associateWith { response.headers()[it].orEmpty() },
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        ApiResponse.Failure.NetworkException(exception = e, message = "네트워크 연결을 확인해주세요.")
    } catch (e: Exception) {
        // 에러 원인 파악 후 제거
        FirebaseCrashlytics.getInstance().apply {
            log("UnknownException in handleApiResponse")
            log("exception: ${e.javaClass.name}: ${e.localizedMessage}")
            e.cause?.let { cause ->
                log("caused by: ${cause.javaClass.name}: ${cause.localizedMessage}")
            }
            recordException(e)
        }

        ApiResponse.Failure.UnknownException(
            exception = e,
            message = "알 수 없는 오류가 발생했습니다.\n잠시후 다시 시도해주세요.",
        )
    }
