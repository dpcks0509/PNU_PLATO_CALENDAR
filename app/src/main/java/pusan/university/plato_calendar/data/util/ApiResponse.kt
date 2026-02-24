package pusan.university.plato_calendar.data.util

import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import retrofit2.Response

sealed interface ApiResponse<out T> {
    data class Success<out T>(val data: T?) : ApiResponse<T>
    data class HttpError(
        val code: Int,
        val message: String?,
        val headers: Map<String, String> = emptyMap(),
    ) : ApiResponse<Nothing>
    data object NetworkException : ApiResponse<Nothing>
}

suspend inline fun <T> handleApiResponse(
    crossinline call: suspend () -> Response<T>
): ApiResponse<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            ApiResponse.Success(body)
        } else {
            ApiResponse.HttpError(
                code = response.code(),
                message = response.errorBody()?.string(),
                headers = response.headers().names().associateWith { response.headers()[it].orEmpty() },
            )
        }
    } catch (_: NoNetworkConnectivityException) {
        ApiResponse.NetworkException
    }
}