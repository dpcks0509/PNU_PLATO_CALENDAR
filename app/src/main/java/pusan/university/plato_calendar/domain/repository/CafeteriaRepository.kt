package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.Campus
import pusan.university.plato_calendar.domain.entity.DailyCafeteriaMenu

interface CafeteriaRepository {
    suspend fun getDailyCafeteriaMenus(
        campus: Campus,
        buildingCode: String,
        restaurantCode: String,
    ): Result<List<DailyCafeteriaMenu>>
}
