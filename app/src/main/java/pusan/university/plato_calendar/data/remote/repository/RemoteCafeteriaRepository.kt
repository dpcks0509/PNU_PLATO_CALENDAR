package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.data.remote.service.CafeteriaService
import pusan.university.plato_calendar.data.util.ApiResponse
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.data.util.handleApiResponse
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.entity.CourseMenu
import pusan.university.plato_calendar.domain.entity.DailyCafeteriaPlan
import pusan.university.plato_calendar.domain.entity.MealInfo
import pusan.university.plato_calendar.domain.entity.MealType
import pusan.university.plato_calendar.domain.entity.OperationInfo
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RemoteCafeteriaRepository
@Inject
constructor(
    private val cafeteriaService: CafeteriaService,
) : CafeteriaRepository {
    override suspend fun getCafeteriaWeeklyPlan(cafeteria: Cafeteria): ApiResult<CafeteriaWeeklyPlan> {
        return when (
            val response = handleApiResponse {
                cafeteriaService.getCafeteriaWeeklyPlan(
                    campus = cafeteria.campus.name,
                    buildingCode = cafeteria.buildingCode,
                    restaurantCode = cafeteria.restaurantCode,
                )
            }
        ) {
            is ApiResponse.Success -> {
                val responseBody = response.data?.string()
                if (responseBody.isNullOrBlank()) {
                    ApiResult.Error(Exception(GET_CAFETERIA_MENUS_FAILED_ERROR))
                } else {
                    ApiResult.Success(
                        CafeteriaWeeklyPlan(
                            cafeteria = cafeteria,
                            notice = responseBody.parseNotice(),
                            weeklyPlans = responseBody.parseHtmlToWeeklyPlans(),
                        )
                    )
                }
            }
            is ApiResponse.NetworkException -> ApiResult.Error(NoNetworkConnectivityException())
            is ApiResponse.HttpError -> {
                ApiResult.Error(Exception(GET_CAFETERIA_MENUS_FAILED_ERROR))
            }
        }
    }

    companion object {
        private const val GET_CAFETERIA_MENUS_FAILED_ERROR = "식단 정보를 불러오는데 실패했습니다."
    }
}

private fun String.parseHtmlToWeeklyPlans(): List<DailyCafeteriaPlan> {
    data class MenuWithDate(
        val date: String,
        val day: String,
        val mealType: MealType,
        val menu: CourseMenu,
        val operationInfo: OperationInfo,
    )

    val menusWithDate = mutableListOf<MenuWithDate>()

    val dateInfoList = mutableListOf<Pair<String, String>>()
    val theadRows =
        Regex("<thead>(.*?)</thead>", RegexOption.DOT_MATCHES_ALL).find(this)?.groupValues?.get(1)

    theadRows?.let { thead ->
        val dayMatches = Regex("<div class=\"day\">([^<]+)</div>").findAll(thead)
        val dateMatches = Regex("<div class=\"date\">([^<]+)</div>").findAll(thead)

        val days = dayMatches.map { it.groupValues[1].trim() }.toList()
        val dates = dateMatches.map { it.groupValues[1].trim() }.toList()

        days.zip(dates).forEach { (day, date) ->
            dateInfoList.add(day to date)
        }
    }

    val tbodyContent =
        Regex("<tbody>(.*?)</tbody>", RegexOption.DOT_MATCHES_ALL).find(this)?.groupValues?.get(1)
            ?: return emptyList()
    val rows = tbodyContent.split("<tr>").filter { it.contains("<th scope=\"row\">") }

    rows.forEach { row ->
        val thContent =
            Regex(
                "<th scope=\"row\">(.*?)</th>",
                RegexOption.DOT_MATCHES_ALL,
            ).find(row)?.groupValues?.get(1) ?: return@forEach
        val thParts = thContent.split(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE))

        val mealTypeText = thParts.firstOrNull()?.replace(Regex("<[^>]+>"), "")?.trim()
        val timeRange = thParts.getOrNull(1)?.replace(Regex("<[^>]+>"), "")?.trim() ?: ""

        val mealType =
            when (mealTypeText) {
                "조식" -> MealType.BREAKFAST
                "중식" -> MealType.LUNCH
                "석식" -> MealType.DINNER
                else -> return@forEach
            }

        val tdElements = Regex("<td>(.*?)</td>", RegexOption.DOT_MATCHES_ALL).findAll(row).toList()

        if (timeRange.isBlank() || timeRange.contains(NOT_OPERATE) || timeRange.contains("x")) {
            dateInfoList.forEach { (day, date) ->
                menusWithDate.add(
                    MenuWithDate(
                        date = date,
                        day = day,
                        mealType = mealType,
                        menu =
                            CourseMenu(
                                courseTitle = null,
                                menus = null,
                            ),
                        operationInfo =
                            OperationInfo(
                                isOperating = false,
                                notOperatingReason = null,
                                operatingTime = null,
                            ),
                    ),
                )
            }
        } else {
            tdElements.forEachIndexed { index, tdMatch ->
                if (index >= dateInfoList.size) return@forEachIndexed
                val tdContent = tdMatch.groupValues[1]
                val (day, date) = dateInfoList[index]
                val liElements =
                    Regex("<li>(.*?)</li>", RegexOption.DOT_MATCHES_ALL).findAll(tdContent)
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
                        val pContent =
                            Regex(
                                "<p>(.*?)</p>",
                                RegexOption.DOT_MATCHES_ALL,
                            ).find(liContent)?.groupValues?.get(1)
                        val dishes =
                            pContent
                                ?.split(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE))
                                ?.map { it.replace(Regex("<[^>]+>"), "").trim() }
                                ?.filter { it.isNotBlank() }
                                ?.joinToString(", ") ?: ""

                        if (dishes.isNotEmpty()) {
                            menusWithDate.add(
                                MenuWithDate(
                                    date = date,
                                    day = day,
                                    mealType = mealType,
                                    menu =
                                        CourseMenu(
                                            courseTitle = h3Content,
                                            menus = dishes,
                                        ),
                                    operationInfo =
                                        OperationInfo(
                                            isOperating = true,
                                            notOperatingReason = null,
                                            operatingTime = timeRange,
                                        ),
                                ),
                            )
                        } else {
                            menusWithDate.add(
                                MenuWithDate(
                                    date = date,
                                    day = day,
                                    mealType = mealType,
                                    menu =
                                        CourseMenu(
                                            courseTitle = null,
                                            menus = null,
                                        ),
                                    operationInfo =
                                        OperationInfo(
                                            isOperating = false,
                                            notOperatingReason = h3Content,
                                            operatingTime = null,
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
                            mealType = mealType,
                            menu =
                                CourseMenu(
                                    courseTitle = null,
                                    menus = null,
                                ),
                            operationInfo =
                                OperationInfo(
                                    isOperating = false,
                                    notOperatingReason = null,
                                    operatingTime = null,
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
                val reasonOfDay = dailyMenus.firstNotNullOfOrNull { it.operationInfo.notOperatingReason }

                val finalReason = reasonOfDay ?: NOT_OPERATE

                dailyMenus.map { menuWithDate ->
                    if (!menuWithDate.operationInfo.isOperating && menuWithDate.operationInfo.notOperatingReason == null) {
                        menuWithDate.copy(
                            operationInfo =
                                menuWithDate.operationInfo.copy(notOperatingReason = finalReason),
                        )
                    } else {
                        menuWithDate
                    }
                }
            }

    val dailyCafeteriaPlans =
        syncedMenusWithDate
            .sortedWith(compareBy({ it.date }, { it.mealType.ordinal }))
            .groupBy { it.date }
            .map { (date, menusWithDate) ->
                val day = menusWithDate.firstOrNull()?.day ?: ""

                val mealInfos =
                    menusWithDate
                        .groupBy { it.mealType }
                        .map { (mealType, menus) ->
                            MealInfo(
                                mealType = mealType,
                                operationInfo = menus.first().operationInfo,
                                courseMenus = menus.map { it.menu },
                            )
                        }.sortedBy { it.mealType.ordinal }

                DailyCafeteriaPlan(
                    date = date,
                    day = day,
                    mealInfos = mealInfos,
                )
            }.sortedBy { it.date }

    if (dailyCafeteriaPlans.isEmpty()) return emptyList()

    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val firstDate = LocalDate.parse(dailyCafeteriaPlans.first().date, formatter)
    val sundayDate = firstDate.minusDays(1)
    val sundayDateString = sundayDate.format(formatter)

    val sundayMenu =
        DailyCafeteriaPlan(
            date = sundayDateString,
            day = "일",
            mealInfos =
                listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER).map { mealType ->
                    MealInfo(
                        mealType = mealType,
                        operationInfo =
                            OperationInfo(
                                isOperating = false,
                                notOperatingReason = NOT_OPERATE,
                                operatingTime = null,
                            ),
                        courseMenus = emptyList(),
                    )
                },
        )

    return listOf(sundayMenu) + dailyCafeteriaPlans
}

private fun String.parseNotice(): String {
    val noticeRegex =
        Regex("<div class=\"dish-notice\">(.*?)</div>\\s*</div>", RegexOption.DOT_MATCHES_ALL)
    val noticeMatch = noticeRegex.find(this) ?: return HAS_NO_NOTICE

    val noticeContent = noticeMatch.groupValues[1]

    val h4Content =
        Regex(
            "<h4>(.*?)</h4>",
            RegexOption.DOT_MATCHES_ALL,
        ).find(noticeContent)?.groupValues?.get(1) ?: ""
    val pContent =
        Regex("<p>(.*?)</p>", RegexOption.DOT_MATCHES_ALL).find(noticeContent)?.groupValues?.get(1)
            ?: ""

    val noticeLines = mutableListOf<String>()

    if (h4Content.isNotBlank()) {
        val h4Text =
            h4Content
                .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
                .replace(Regex("<[^>]+>"), "")
                .trim()
        if (h4Text.isNotBlank()) {
            noticeLines.add(h4Text)
        }
    }

    if (pContent.isNotBlank()) {
        val pText =
            pContent
                .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
                .replace(Regex("<[^>]+>"), "")
                .trim()
        if (pText.isNotBlank()) {
            noticeLines.add(pText)
        }
    }

    return noticeLines
        .joinToString("\n")
        .lines()
        .filter { it.isNotBlank() }
        .joinToString("\n")
}

private const val HAS_NO_NOTICE = "공지 없음"
private const val NOT_OPERATE = "미운영"
