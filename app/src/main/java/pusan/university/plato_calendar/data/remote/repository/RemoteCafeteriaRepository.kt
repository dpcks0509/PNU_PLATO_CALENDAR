package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.data.remote.service.CafeteriaService
import pusan.university.plato_calendar.domain.entity.CafeteriaMenu
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import javax.inject.Inject

class RemoteCafeteriaRepository
    @Inject
    constructor(
        private val cafeteriaService: CafeteriaService,
    ) : CafeteriaRepository {
        override suspend fun getCafeteriaMenusOnBuilding(): Result<List<CafeteriaMenu>> {
            return try {
                val response = cafeteriaService.readCafeteriaMenusOnBuilding()

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (responseBody.isNullOrBlank()) {
                        return Result.success(emptyList())
                    }

                    val cafeteriaMenus = responseBody.parseHtmlToCafeteriaMenus()
                    Result.success(cafeteriaMenus)
                } else {
                    Result.failure(Exception(GET_CAFETERIA_MENUS_FAILED_ERROR))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        companion object {
            private const val GET_CAFETERIA_MENUS_FAILED_ERROR = "교내 식당 식단을 불러오는데 실패했습니다."
        }
    }

private fun String.parseHtmlToCafeteriaMenus(): List<CafeteriaMenu> {
}
