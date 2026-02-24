package pusan.university.plato_calendar.data.util

sealed interface ApiResult<out T> {
    data class Success<out T>(
        val data: T,
    ) : ApiResult<T>

    data class Error(
        val exception: Throwable,
    ) : ApiResult<Nothing>
}

fun <T, R> ApiResponse<T>.toApiResult(
    errorMessage: String,
    transform: (T?) -> ApiResult<R>,
): ApiResult<R> =
    when (this) {
        is ApiResponse.Success -> {
            transform(data)
        }

        is ApiResponse.Failure.NetworkException -> {
            ApiResult.Error(exception)
        }

        is ApiResponse.Failure.HttpException -> {
            val finalMessage = message?.takeIf { it.isNotBlank() } ?: errorMessage
            ApiResult.Error(ApiException(code, finalMessage))
        }

        is ApiResponse.Failure.UnknownException -> {
            ApiResult.Error(exception)
        }
    }
