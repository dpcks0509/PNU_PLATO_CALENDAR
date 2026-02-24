package pusan.university.plato_calendar.data.util

sealed interface ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>
    data class Error(val exception: Exception) : ApiResult<Nothing>
}