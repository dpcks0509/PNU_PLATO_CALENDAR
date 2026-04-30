package pusan.university.plato_calendar.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayService {
    @GET("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
    suspend fun readHolidays(
        @Query("solYear") solYear: Int,
        @Query("ServiceKey", encoded = true) serviceKey: String,
        @Query("numOfRows") numOfRows: Int = 100,
    ): Response<ResponseBody>
}
