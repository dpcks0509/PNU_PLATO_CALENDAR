package pusan.university.plato_calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pusan.university.plato_calendar.presentation.calendar.model.DaySchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun MonthItem(
    monthSchedule: List<List<DaySchedule?>>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        monthSchedule.forEach { weekSchedule ->
            WeekItem(
                weekSchedule = weekSchedule,
                onDateClick = onDateClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonthItemPreview() {
    PlatoCalendarTheme {
        val base = LocalDate.of(2024, 1, 1)
        val schedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "신정",
                    startAt = LocalDate.of(2024, 1, 1),
                    endAt = LocalDate.of(2024, 1, 1),
                ),
                CustomScheduleUiModel(
                    id = 1L,
                    title = "새해 계획 세우기",
                    description = "",
                    startAt = LocalDateTime.of(2024, 1, 3, 14, 0),
                    endAt = LocalDateTime.of(2024, 1, 3, 16, 0),
                    isCompleted = false
                ),
            )

        val monthSchedule =
            List(6) { week ->
                List(7) { day ->
                    val date = base.minusDays(1).plusDays((week * 7 + day).toLong())
                    DaySchedule(
                        date = date,
                        isToday = date.dayOfMonth == 8,
                        isSelected = date.dayOfMonth == 11,
                        isInMonth = date.monthValue == 1,
                        schedules = schedules,
                    )
                }
            }

        MonthItem(
            monthSchedule = monthSchedule,
            onDateClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
