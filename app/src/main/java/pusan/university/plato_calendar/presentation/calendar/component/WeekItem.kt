package pusan.university.plato_calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pusan.university.plato_calendar.presentation.calendar.model.DaySchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun WeekItem(
    weekSchedule: List<DaySchedule?>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        weekSchedule.forEach { daySchedule ->
            DayItem(
                daySchedule = daySchedule,
                onDateClick = onDateClick,
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(3f / 3.6f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeekItemPreview() {
    PlatoCalendarTheme {
        val baseDate = LocalDate.of(2024, 1, 7)
        val schedules =
            List(7) { index ->
                CustomScheduleUiModel(
                    id = index.toLong(),
                    title = "일정 $index",
                    description = "",
                    startAt = LocalDateTime.of(2024, 1, 7 + index, 10, 0),
                    endAt = LocalDateTime.of(2024, 1, 7 + index, 12, 0),
                    isCompleted = false
                )
            }

        val weekSchedule =
            List(7) { index ->
                val date = baseDate.plusDays(index.toLong())
                DaySchedule(
                    date = date,
                    isToday = index == 1,
                    isSelected = index == 3,
                    isInMonth = true,
                    schedules = schedules,
                )
            }

        WeekItem(
            weekSchedule = weekSchedule,
            onDateClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
