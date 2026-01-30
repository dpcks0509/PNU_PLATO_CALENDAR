package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaWeeklyPlan

interface CafeteriaRepository {
    suspend fun getCafeteriaWeeklyPlan(cafeteria: Cafeteria): Result<CafeteriaWeeklyPlan>
}
