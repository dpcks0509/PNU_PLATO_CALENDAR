package pusan.university.plato_calendar.domain.entity

data class OperationInfo(
    val isOperating: Boolean,
    val notOperatingReason: String?,
    val operatingTime: String?,
)
