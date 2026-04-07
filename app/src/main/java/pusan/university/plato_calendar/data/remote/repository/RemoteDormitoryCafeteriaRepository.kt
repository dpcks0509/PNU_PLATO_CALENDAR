package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.data.util.crawler.DormMealPlanCrawler
import pusan.university.plato_calendar.data.util.parser.parseDormHtmlToWeeklyPlans
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.entity.DormitoryCafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.repository.DormitoryCafeteriaRepository
import javax.inject.Inject

class RemoteDormitoryCafeteriaRepository
    @Inject
    constructor(
        private val dormMealPlanCrawler: DormMealPlanCrawler,
    ) : DormitoryCafeteriaRepository {
        override suspend fun getWeeklyPlan(dormitory: Dormitory): ApiResult<DormitoryCafeteriaWeeklyPlan> =
            try {
                val html = dormMealPlanCrawler.fetchMealPlan(dormitory)
                val result = DormitoryCafeteriaWeeklyPlan(
                    dormitory = dormitory,
                    weeklyPlans = html.parseDormHtmlToWeeklyPlans(),
                )
                println(result)
                ApiResult.Success(result)
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
    }
