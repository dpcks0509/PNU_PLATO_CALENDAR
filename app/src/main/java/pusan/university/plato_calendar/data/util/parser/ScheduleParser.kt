package pusan.university.plato_calendar.data.util.parser

import pusan.university.plato_calendar.domain.entity.Schedule.AcademicSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.util.extension.formatCourseCode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

internal fun String.parseIcsToPersonalSchedules(): List<PersonalSchedule> {
    val unfoldedLines = mutableListOf<String>()
    lines().forEach { rawLine ->
        if ((rawLine.startsWith(" ") || rawLine.startsWith("\t")) && unfoldedLines.isNotEmpty()) {
            val previous = unfoldedLines.removeAt(unfoldedLines.lastIndex)
            unfoldedLines.add(previous + rawLine.trimStart())
        } else {
            unfoldedLines.add(rawLine)
        }
    }

    val personalSchedules = mutableListOf<PersonalSchedule>()
    var inEvent = false
    val currentFields = mutableMapOf<String, String>()

    unfoldedLines.forEach { line ->
        val trimmed = line.trim()
        when {
            trimmed.equals("BEGIN:VEVENT", ignoreCase = true) -> {
                inEvent = true
                currentFields.clear()
            }

            trimmed.equals("END:VEVENT", ignoreCase = true) -> {
                if (inEvent) {
                    personalSchedules.add(buildScheduleFromFields(currentFields.toMap()))
                }
                inEvent = false
                currentFields.clear()
            }

            inEvent -> {
                val colonIndex = trimmed.indexOf(':')
                if (colonIndex > 0) {
                    val key =
                        trimmed.substring(0, colonIndex).substringBefore(';').uppercase()
                    val value = trimmed.substring(colonIndex + 1)
                    currentFields[key] = value
                }
            }
        }
    }

    return personalSchedules
}

internal fun String.parseHtmlToAcademicSchedules(): List<AcademicSchedule> {
    val academicSchedules = mutableListOf<AcademicSchedule>()

    val tableRows = this.split("<tr>").drop(1)

    tableRows.forEach { row ->
        if (row.contains("class=\"term\"") && row.contains("class=\"text\"")) {
            val termMatch = Regex("class=\"term\"[^>]*>([^<]+)</").find(row)
            val termText = termMatch?.groupValues?.get(1)?.trim()

            val textMatch = Regex("class=\"text\"[^>]*>([^<]+)</").find(row)
            val textContent = textMatch?.groupValues?.get(1)?.trim()

            if (termText != null && textContent != null) {
                val (startAt, endAt) = termText.parseDateRange() ?: return@forEach

                academicSchedules.add(
                    AcademicSchedule(
                        title = textContent,
                        startAt = startAt,
                        endAt = endAt,
                    ),
                )
            }
        }
    }

    return academicSchedules
}

private fun buildScheduleFromFields(fields: Map<String, String>): PersonalSchedule {
    val courseCode = fields["CATEGORIES"]?.split("_")[2].formatCourseCode()

    val description = fields["DESCRIPTION"]?.processIcsDescription()

    val startAt = fields["DTSTART"].orEmpty().parseUtcToKstLocalDateTime()
    val endAt = fields["DTEND"].orEmpty().parseUtcToKstLocalDateTime()

    val adjustedStartAt =
        if (startAt == endAt && startAt.hour == 0 && startAt.minute == 0) {
            startAt.minusMinutes(1)
        } else {
            startAt
        }

    val adjustedEndAt =
        if (endAt.hour == 0 && endAt.minute == 0) {
            endAt.minusMinutes(1)
        } else {
            endAt
        }

    return if (courseCode == null) {
        CustomSchedule(
            id = fields["UID"].orEmpty().split("@")[0].toLong(),
            title = fields["SUMMARY"].orEmpty(),
            description = description,
            startAt = startAt,
            endAt = endAt,
            isCompleted = false,
        )
    } else {
        CourseSchedule(
            id = fields["UID"].orEmpty().split("@")[0].toLong(),
            title = fields["SUMMARY"].orEmpty(),
            description = description,
            startAt = adjustedStartAt,
            endAt = adjustedEndAt,
            courseCode = courseCode,
            isCompleted = false,
        )
    }
}

private fun String.processIcsDescription(): String =
    this
        .replace(Regex("(\\\\n){2,}"), "\\\\n")
        .replace(Regex("^(\\\\n)+"), "")
        .replace(Regex("(\\\\n)+$"), "")
        .replace("\\\\", "\u0001")
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\;", ";")
        .replace("\\,", ",")
        .replace("\u0001", "\\")

private fun String.parseUtcToKstLocalDateTime(): LocalDateTime {
    val year = substring(0, 4).toInt()
    val month = substring(4, 6).toInt()
    val day = substring(6, 8).toInt()
    val hour = substring(9, 11).toInt()
    val minute = substring(11, 13).toInt()

    val utcTime = LocalDateTime.of(year, month, day, hour, minute)
    return utcTime.atOffset(ZoneOffset.UTC).plusHours(9).toLocalDateTime()
}

private fun String.parseDateRange(): Pair<LocalDate, LocalDate>? {
    val dates = this.split(" - ").map { it.trim() }
    if (dates.size != 2) return null

    val startDate = dates[0].parseKoreanDateToLocalDate() ?: return null
    val endDate = dates[1].parseKoreanDateToLocalDate() ?: return null

    return startDate to endDate
}

private fun String.parseKoreanDateToLocalDate(): LocalDate? {
    val parts = this.split(".")
    if (parts.size != 3) return null

    val year = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val month = parts.getOrNull(1)?.toIntOrNull() ?: return null
    val day = parts.getOrNull(2)?.toIntOrNull() ?: return null

    if (month !in 1..12) return null
    if (day !in 1..31) return null

    return if (day <= LocalDate.of(year, month, 1).lengthOfMonth()) {
        LocalDate.of(year, month, day)
    } else {
        null
    }
}
