package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Holiday

interface HolidayRepository {
    suspend fun getHolidays(year: Int): ApiResult<List<Holiday>>
}
