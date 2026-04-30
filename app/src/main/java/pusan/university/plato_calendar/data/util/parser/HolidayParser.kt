package pusan.university.plato_calendar.data.util.parser

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import pusan.university.plato_calendar.domain.entity.Holiday
import java.time.LocalDate

internal fun String.parseXmlToHolidays(): List<Holiday> {
    val document = Jsoup.parse(this, "", Parser.xmlParser())
    return document.select("item").mapNotNull { item ->
        val isHoliday = item.selectFirst("isHoliday")?.text()?.trim()
        if (!isHoliday.equals("Y", ignoreCase = true)) return@mapNotNull null

        val locdate = item.selectFirst("locdate")?.text()?.trim() ?: return@mapNotNull null
        val name = item.selectFirst("dateName")?.text()?.trim().orEmpty()
        val date = locdate.parseLocdateToLocalDate() ?: return@mapNotNull null

        Holiday(date = date, name = name)
    }
}

private fun String.parseLocdateToLocalDate(): LocalDate? {
    if (length != 8) return null
    val year = substring(0, 4).toIntOrNull() ?: return null
    val month = substring(4, 6).toIntOrNull() ?: return null
    val day = substring(6, 8).toIntOrNull() ?: return null
    return runCatching { LocalDate.of(year, month, day) }.getOrNull()
}
