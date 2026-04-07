package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.entity.DormitoryCafeteriaWeeklyPlan

interface DormitoryCafeteriaRepository {
    suspend fun getWeeklyPlan(dormitory: Dormitory): ApiResult<DormitoryCafeteriaWeeklyPlan>
}
