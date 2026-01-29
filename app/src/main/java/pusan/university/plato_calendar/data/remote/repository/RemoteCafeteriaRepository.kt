package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.data.remote.service.CafeteriaService
import pusan.university.plato_calendar.domain.entity.CafeteriaMenu
import pusan.university.plato_calendar.domain.entity.Campus
import pusan.university.plato_calendar.domain.entity.DailyCafeteriaMenu
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import javax.inject.Inject

class RemoteCafeteriaRepository
    @Inject
    constructor(
        private val cafeteriaService: CafeteriaService,
    ) : CafeteriaRepository {
        override suspend fun getDailyCafeteriaMenus(
            campus: Campus,
            buildingCode: String,
            restaurantCode: String,
        ): Result<List<DailyCafeteriaMenu>> {
            return try {
                val response =
                    cafeteriaService.getDailyCafeteriaMenus(
                        campus = campus.name,
                        buildingCode = buildingCode,
                        restaurantCode = restaurantCode,
                    )

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (responseBody.isNullOrBlank()) {
                        return Result.success(emptyList())
                    }

                    val dailyCafeteriaMenus = responseBody.parseHtmlToDailyCafeteriaMenus()
                    Result.success(dailyCafeteriaMenus)
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

private fun String.parseHtmlToDailyCafeteriaMenus(): List<DailyCafeteriaMenu> {
    data class MenuWithDate(
        val date: String,
        val day: String,
        val menu: CafeteriaMenu,
    )

    val menusWithDate = mutableListOf<MenuWithDate>()

    val dateInfoList = mutableListOf<Pair<String, String>>()
    val theadRows = Regex("<thead>(.*?)</thead>", RegexOption.DOT_MATCHES_ALL).find(this)?.groupValues?.get(1)

    theadRows?.let { thead ->
        val dayMatches = Regex("<div class=\"day\">([^<]+)</div>").findAll(thead)
        val dateMatches = Regex("<div class=\"date\">([^<]+)</div>").findAll(thead)

        val days = dayMatches.map { it.groupValues[1].trim() }.toList()
        val dates = dateMatches.map { it.groupValues[1].trim() }.toList()

        days.zip(dates).forEach { (day, date) ->
            dateInfoList.add(day to date)
        }
    }

    val tbodyContent = Regex("<tbody>(.*?)</tbody>", RegexOption.DOT_MATCHES_ALL).find(this)?.groupValues?.get(1) ?: return emptyList()
    val rows = tbodyContent.split("<tr>").filter { it.contains("<th scope=\"row\">") }

    rows.forEach { row ->
        val thContent = Regex("<th scope=\"row\">(.*?)</th>", RegexOption.DOT_MATCHES_ALL).find(row)?.groupValues?.get(1) ?: return@forEach
        val thParts = thContent.split(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE))

        val mealTypeText = thParts.firstOrNull()?.replace(Regex("<[^>]+>"), "")?.trim()
        val timeRange = thParts.getOrNull(1)?.replace(Regex("<[^>]+>"), "")?.trim() ?: ""

        val mealType =
            when (mealTypeText) {
                "조식" -> pusan.university.plato_calendar.domain.entity.MealType.BREAKFAST
                "중식" -> pusan.university.plato_calendar.domain.entity.MealType.LUNCH
                "석식" -> pusan.university.plato_calendar.domain.entity.MealType.DINNER
                else -> return@forEach
            }

        val tdElements = Regex("<td>(.*?)</td>", RegexOption.DOT_MATCHES_ALL).findAll(row).toList()

        if (timeRange.isBlank() || timeRange.contains("미운영") || timeRange.contains("x")) {
            dateInfoList.forEach { (day, date) ->
                menusWithDate.add(
                    MenuWithDate(
                        date = date,
                        day = day,
                        menu =
                            CafeteriaMenu(
                                mealType = mealType,
                                isOperating = false,
                                notOperatingReason = null,
                                operatingTime = null,
                                courseName = null,
                                price = null,
                                dishes = null,
                            ),
                    ),
                )
            }
        } else {
            tdElements.forEachIndexed { index, tdMatch ->
                if (index >= dateInfoList.size) return@forEachIndexed
                val tdContent = tdMatch.groupValues[1]
                val (day, date) = dateInfoList[index]
                val liElements = Regex("<li>(.*?)</li>", RegexOption.DOT_MATCHES_ALL).findAll(tdContent)
                var hasMenu = false

                liElements.forEach { liMatch ->
                    val liContent = liMatch.groupValues[1]
                    val h3Content =
                        Regex("<h3[^>]*>([^<]+)</h3>")
                            .find(liContent)
                            ?.groupValues
                            ?.get(1)
                            ?.trim()

                    if (h3Content != null) {
                        hasMenu = true
                        val pContent = Regex("<p>(.*?)</p>", RegexOption.DOT_MATCHES_ALL).find(liContent)?.groupValues?.get(1)
                        val dishes =
                            pContent
                                ?.split(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE))
                                ?.map { it.replace(Regex("<[^>]+>"), "").trim() }
                                ?.filter { it.isNotBlank() }
                                ?.joinToString(", ") ?: ""

                        if (dishes.isNotEmpty()) {
                            val courseName = h3Content.substringBefore("-").trim()
                            val price = h3Content.substringAfter("-").trim()
                            menusWithDate.add(
                                MenuWithDate(
                                    date = date,
                                    day = day,
                                    menu =
                                        CafeteriaMenu(
                                            mealType = mealType,
                                            isOperating = true,
                                            notOperatingReason = null,
                                            operatingTime = timeRange,
                                            courseName = courseName,
                                            price = price,
                                            dishes = dishes,
                                        ),
                                ),
                            )
                        } else {
                            menusWithDate.add(
                                MenuWithDate(
                                    date = date,
                                    day = day,
                                    menu =
                                        CafeteriaMenu(
                                            mealType = mealType,
                                            isOperating = false,
                                            notOperatingReason = h3Content,
                                            operatingTime = null,
                                            courseName = null,
                                            price = null,
                                            dishes = null,
                                        ),
                                ),
                            )
                        }
                    }
                }

                if (!hasMenu) {
                    menusWithDate.add(
                        MenuWithDate(
                            date = date,
                            day = day,
                            menu =
                                CafeteriaMenu(
                                    mealType = mealType,
                                    isOperating = false,
                                    notOperatingReason = null,
                                    operatingTime = null,
                                    courseName = null,
                                    price = null,
                                    dishes = null,
                                ),
                        ),
                    )
                }
            }
        }
    }

    val syncedMenusWithDate =
        menusWithDate
            .groupBy { it.date }
            .flatMap { (_, dailyMenus) ->
                val reasonOfDay = dailyMenus.firstNotNullOfOrNull { it.menu.notOperatingReason }

                val finalReason = reasonOfDay ?: "미운영"

                dailyMenus.map { menuWithDate ->
                    if (!menuWithDate.menu.isOperating && menuWithDate.menu.notOperatingReason == null) {
                        menuWithDate.copy(menu = menuWithDate.menu.copy(notOperatingReason = finalReason))
                    } else {
                        menuWithDate
                    }
                }
            }

    return syncedMenusWithDate
        .sortedWith(compareBy({ it.date }, { it.menu.mealType.ordinal }))
        .groupBy { it.date }
        .map { (date, menusWithDate) ->
            val day = menusWithDate.firstOrNull()?.day ?: ""
            DailyCafeteriaMenu(
                date = date,
                day = day,
                breakfast =
                    menusWithDate
                        .filter { it.menu.mealType == pusan.university.plato_calendar.domain.entity.MealType.BREAKFAST }
                        .map { it.menu },
                lunch =
                    menusWithDate
                        .filter {
                            it.menu.mealType == pusan.university.plato_calendar.domain.entity.MealType.LUNCH
                        }.map { it.menu },
                dinner =
                    menusWithDate
                        .filter {
                            it.menu.mealType == pusan.university.plato_calendar.domain.entity.MealType.DINNER
                        }.map { it.menu },
            )
        }.sortedBy { it.date }
}
