package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.CafeteriaMenu
import pusan.university.plato_calendar.domain.entity.Campus

interface CafeteriaRepository {
    suspend fun getCafeteriaMenusOnBuilding(
        campus: Campus,
        buildingCode: String,
        restaurantCode: String,
        date: String = "",
    ): Result<List<CafeteriaMenu>>
}
