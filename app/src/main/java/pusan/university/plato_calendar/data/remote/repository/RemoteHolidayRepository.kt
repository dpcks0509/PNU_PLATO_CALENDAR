package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.BuildConfig
import pusan.university.plato_calendar.data.remote.service.HolidayService
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.data.util.handleApiResponse
import pusan.university.plato_calendar.data.util.parser.parseXmlToHolidays
import pusan.university.plato_calendar.data.util.toApiResult
import pusan.university.plato_calendar.domain.entity.Holiday
import pusan.university.plato_calendar.domain.repository.HolidayRepository
import javax.inject.Inject

class RemoteHolidayRepository
@Inject
constructor(
    private val holidayService: HolidayService,
) : HolidayRepository {
    private val cache = mutableMapOf<Int, List<Holiday>>()

    override suspend fun getHolidays(year: Int): ApiResult<List<Holiday>> {
        cache[year]?.let { return ApiResult.Success(it) }

        val response = handleApiResponse {
            holidayService.readHolidays(
                solYear = year,
                serviceKey = BuildConfig.HOLIDAY_SERVICE_KEY,
            )
        }

        return response.toApiResult(GET_HOLIDAYS_FAILED_ERROR) { body ->
            val responseBody = body?.string()
            if (responseBody.isNullOrBlank()) {
                ApiResult.Error(Exception(GET_HOLIDAYS_FAILED_ERROR))
            } else {
                val holidays = responseBody.parseXmlToHolidays()
                cache[year] = holidays
                ApiResult.Success(holidays)
            }
        }
    }

    companion object {
        private const val GET_HOLIDAYS_FAILED_ERROR = "공휴일을 불러오는데 실패했습니다."
    }
}
