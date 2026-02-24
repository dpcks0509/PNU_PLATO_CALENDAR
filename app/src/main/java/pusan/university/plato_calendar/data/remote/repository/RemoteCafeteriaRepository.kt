package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.data.remote.service.CafeteriaService
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.data.util.handleApiResponse
import pusan.university.plato_calendar.data.util.parser.parseHtmlToWeeklyPlans
import pusan.university.plato_calendar.data.util.parser.parseNotice
import pusan.university.plato_calendar.data.util.toApiResult
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import javax.inject.Inject

class RemoteCafeteriaRepository
    @Inject
    constructor(
        private val cafeteriaService: CafeteriaService,
    ) : CafeteriaRepository {
        override suspend fun getCafeteriaWeeklyPlan(cafeteria: Cafeteria): ApiResult<CafeteriaWeeklyPlan> {
            val response =
                handleApiResponse {
                    cafeteriaService.getCafeteriaWeeklyPlan(
                        campus = cafeteria.campus.name,
                        buildingCode = cafeteria.buildingCode,
                        restaurantCode = cafeteria.restaurantCode,
                    )
                }

            return response.toApiResult(GET_CAFETERIA_MENUS_FAILED_ERROR) { body ->
                val responseBody = body?.string()
                if (responseBody.isNullOrBlank()) {
                    ApiResult.Error(Exception(GET_CAFETERIA_MENUS_FAILED_ERROR))
                } else {
                    ApiResult.Success(
                        CafeteriaWeeklyPlan(
                            cafeteria = cafeteria,
                            notice = responseBody.parseNotice(),
                            weeklyPlans = responseBody.parseHtmlToWeeklyPlans(),
                        ),
                    )
                }
            }
        }

        companion object {
            private const val GET_CAFETERIA_MENUS_FAILED_ERROR = "식단 정보를 불러오는데 실패했습니다."
        }
    }
