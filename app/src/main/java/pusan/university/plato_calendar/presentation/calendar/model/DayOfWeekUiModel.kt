package pusan.university.plato_calendar.presentation.calendar.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.Red
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

data class DayOfWeekUiModel(
    val title: String,
    val color: Color
) {
    companion object {
        @Composable
        fun from(dayOfWeek: DayOfWeek): DayOfWeekUiModel {
            return DayOfWeekUiModel(
                title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                color = if (dayOfWeek.isWeekend()) Red else Black
            )
        }

        fun DayOfWeek.isWeekend(): Boolean {
            return this == DayOfWeek.SATURDAY || this == DayOfWeek.SUNDAY
        }

        fun dayOfWeeksEntries(): List<DayOfWeek> = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
        )
    }
}