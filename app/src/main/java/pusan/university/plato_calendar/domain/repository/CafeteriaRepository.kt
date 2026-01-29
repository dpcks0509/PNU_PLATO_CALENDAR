package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.CafeteriaMenu
import pusan.university.plato_calendar.domain.entity.Campus

interface CafeteriaRepository {
    suspend fun getCafeteriaMenus(
        campus: Campus,
        buildingCode: String,
        restaurantCode: String,
    ): Result<List<CafeteriaMenu>>
}
