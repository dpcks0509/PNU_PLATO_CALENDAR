package pusan.university.plato_calendar.data.util

import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import retrofit2.Response
import java.io.IOException

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
        ) : Failure

        data class UnknownException(
            val exception: Throwable,
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
    } catch (e: NoNetworkConnectivityException) {
        ApiResponse.Failure.NetworkException(e)
    } catch (e: IOException) {
        ApiResponse.Failure.NetworkException(e)
    } catch (e: Exception) {
        ApiResponse.Failure.UnknownException(e)
    }
