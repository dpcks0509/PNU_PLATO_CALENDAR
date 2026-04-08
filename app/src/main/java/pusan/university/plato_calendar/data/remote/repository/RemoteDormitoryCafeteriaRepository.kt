package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.data.util.crawler.DormitoryMealPlanCrawler
import pusan.university.plato_calendar.data.util.parser.parseDormHtmlToWeeklyPlans
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.entity.DormitoryCafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.repository.DormitoryCafeteriaRepository
import javax.inject.Inject

class RemoteDormitoryCafeteriaRepository
@Inject
constructor(
    private val dormitoryMealPlanCrawler: DormitoryMealPlanCrawler,
) : DormitoryCafeteriaRepository {
    override suspend fun getWeeklyPlan(dormitory: Dormitory): ApiResult<DormitoryCafeteriaWeeklyPlan> =
        try {
            val html = dormitoryMealPlanCrawler.fetchMealPlan(dormitory)
            val notice = "※ P(pork), E(egg), B(beef), F(fish), C(chicken), D(duck)" + when (dormitory) {
                Dormitory.JILLI, Dormitory.UNGBEE, Dormitory.JAYU -> "\n※ 아침식사 신청자 중 하루 일과를 일찍 시작하는 원생을 위해, 07:00(또는 07:30)~08:00에 빵과 시리얼을 제공합니다."
                Dormitory.MILYANG -> ""
                Dormitory.YANGSAN -> "\n※ 조기식사: 월~토 제공, 아침식사 신청자만 이용 가능"
            }

            val result = DormitoryCafeteriaWeeklyPlan(
                dormitory = dormitory,
                notice = notice,
                weeklyPlans = html.parseDormHtmlToWeeklyPlans().map { dailyPlan ->
                    dailyPlan.copy(
                        mealInfos = dailyPlan.mealInfos.map { mealInfo ->
                            mealInfo.copy(operatingTime = dormitory.operatingTime(mealInfo.mealType))
                        },
                    )
                },
            )
            ApiResult.Success(result)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
}
