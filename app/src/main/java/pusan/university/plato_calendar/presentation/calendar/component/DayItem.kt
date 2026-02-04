package pusan.university.plato_calendar.presentation.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.calendar.model.DaySchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.Gray
import pusan.university.plato_calendar.presentation.common.theme.MediumGray
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.Red
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun DayItem(
    daySchedule: DaySchedule?,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (daySchedule != null) {
        Column(
            modifier =
                modifier
                    .noRippleClickable { onDateClick(daySchedule.date) }
                    .then(
                        if (daySchedule.isSelected) {
                            Modifier
                                .clip(
                                    RoundedCornerShape(12.dp),
                                ).background(MediumGray)
                        } else {
                            Modifier
                        },
                    ).padding(top = 6.dp, bottom = 12.dp, start = 6.dp, end = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier =
                    Modifier
                        .then(
                            if (daySchedule.isToday) {
                                Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryColor)
                            } else {
                                Modifier
                            },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = daySchedule.date.dayOfMonth.toString(),
                    color =
                        (
                            if (daySchedule.isToday) {
                                Color.White
                            } else if (daySchedule.isWeekend) {
                                Red
                            } else {
                                Black
                            }
                        ).let { color -> if (daySchedule.isInMonth) color else color.copy(alpha = 0.6f) },
                    fontSize = 16.sp,
                    fontWeight = if (daySchedule.isInMonth) FontWeight.Bold else FontWeight.Normal,
                )
            }

            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(1.5.dp, Alignment.CenterHorizontally),
            ) {
                daySchedule.visibleSchedules.forEach { schedule ->
                    Box(
                        modifier =
                            Modifier
                                .size(4.5.dp)
                                .clip(CircleShape)
                                .background(schedule.color.let { color -> if (daySchedule.isInMonth) color else color.copy(alpha = 0.6f) }),
                    )
                }
            }
        }
    } else {
        Box(
            modifier = modifier.padding(top = 6.dp, bottom = 12.dp, start = 6.dp, end = 6.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Text(
                text = "X",
                color = Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun DayItemPreview() {
    PlatoCalendarTheme {
        val today = LocalDateTime.now().toLocalDate()
        val sampleSchedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "",
                    startAt = today,
                    endAt = today,
                ),
                CustomScheduleUiModel(
                    id = 0L,
                    title = "",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    isCompleted = false,
                ),
            )

        val daySchedule =
            DaySchedule(
                date = today,
                isToday = true,
                isSelected = true,
                isInMonth = true,
                schedules = sampleSchedules,
            )

        DayItem(
            daySchedule = daySchedule,
            onDateClick = { },
            modifier =
                Modifier
                    .width(60.dp)
                    .height(80.dp),
        )
    }
}
