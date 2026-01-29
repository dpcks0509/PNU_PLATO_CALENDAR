package pusan.university.plato_calendar.domain.entity

data class DailyCafeteriaMenu(
    val date: String,
    val day: String,
    val breakfast: List<CafeteriaMenu>,
    val lunch: List<CafeteriaMenu>,
    val dinner: List<CafeteriaMenu>,
)
