package pusan.university.plato_calendar.domain.entity

import java.time.LocalDate

data class Holiday(
    val date: LocalDate,
    val name: String,
)
