package pusan.university.plato_calendar.data.util.parser

import org.jsoup.Jsoup
import pusan.university.plato_calendar.domain.entity.DormitoryCafeteriaDailyPlan
import pusan.university.plato_calendar.domain.entity.DormitoryMealInfo
import pusan.university.plato_calendar.domain.entity.DormitoryMealType

internal fun String.parseDormHtmlToWeeklyPlans(): List<DormitoryCafeteriaDailyPlan> {
    val tbody = Jsoup.parse("<table>$this</table>").selectFirst("tbody") ?: return emptyList()
    val rows = tbody.select("tr")

    data class MealEntry(val date: String, val day: String, val meal: DormitoryMealInfo)

    val entries = mutableListOf<MealEntry>()
    var currentDate = ""
    var currentDay = ""

    for (row in rows) {
        val tds = row.select("td")

        val mealTypeTd: org.jsoup.nodes.Element
        val menuTd: org.jsoup.nodes.Element

        when (tds.size) {
            3 -> {
                val dateParts = tds[0].html()
                    .split(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE))
                currentDate = dateParts[0].replace(Regex("<[^>]+>"), "").trim().replace("-", ".")
                currentDay = dateParts.getOrNull(1)?.replace(Regex("<[^>]+>"), "")?.trim() ?: ""
                mealTypeTd = tds[1]
                menuTd = tds[2]
            }

            2 -> {
                mealTypeTd = tds[0]
                menuTd = tds[1]
            }

            else -> continue
        }

        if (currentDate.isEmpty()) continue

        val mealType = when (mealTypeTd.text().trim()) {
            "조기" -> DormitoryMealType.EARLY_BREAKFAST
            "조식" -> DormitoryMealType.BREAKFAST
            "중식" -> DormitoryMealType.LUNCH
            "석식" -> DormitoryMealType.DINNER
            else -> continue
        }

        val menus = menuTd.html()
            .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("<[^>]+>"), "")
            .trim()

        entries.add(MealEntry(date = currentDate, day = currentDay, meal = DormitoryMealInfo(mealType = mealType, menus = menus)))
    }

    return entries
        .groupBy { it.date }
        .map { (date, group) ->
            DormitoryCafeteriaDailyPlan(
                date = date,
                day = group.first().day,
                mealInfos = group.map { it.meal }.sortedBy { it.mealType.ordinal },
            )
        }
        .sortedBy { it.date }
}
