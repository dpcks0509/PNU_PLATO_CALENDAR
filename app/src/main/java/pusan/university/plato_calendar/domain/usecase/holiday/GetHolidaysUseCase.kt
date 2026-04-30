package pusan.university.plato_calendar.domain.usecase.holiday

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Holiday
import pusan.university.plato_calendar.domain.repository.HolidayRepository

class GetHolidaysUseCase(
    private val holidayRepository: HolidayRepository,
) {
    suspend operator fun invoke(year: Int): ApiResult<List<Holiday>> = holidayRepository.getHolidays(year)
}
