package pusan.university.plato_calendar.presentation.todo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.White
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ToDoScheduleItem(
    schedule: ScheduleUiModel,
    today: LocalDateTime,
    modifier: Modifier = Modifier,
    toggleCompletion: (Long, Boolean) -> Unit = { _, _ -> },
) {
    var isCompleted by remember {
        mutableStateOf(
            when (schedule) {
                is PersonalScheduleUiModel -> schedule.isCompleted
                is AcademicScheduleUiModel -> false
            },
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(schedule.color),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            val title =
                schedule.title.run {
                    if (schedule is CourseScheduleUiModel) {
                        if (schedule.courseName.isEmpty()) this else "${schedule.courseName}_$this"
                    } else {
                        this
                    }
                }

            Text(text = title, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(schedule.color)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    val type =
                        when (schedule) {
                            is AcademicScheduleUiModel -> "학사 일정"
                            is CourseScheduleUiModel -> "수업 일정"
                            is CustomScheduleUiModel -> "개인 일정"
                        }

                    Text(
                        text = type,
                        fontSize = 12.sp,
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = dateRange(schedule, today))
            }
        }

        if (schedule is PersonalScheduleUiModel) {
            Checkbox(
                checked = isCompleted,
                colors =
                    CheckboxDefaults.colors(
                        checkedColor = PrimaryColor,
                        checkmarkColor = White,
                    ),
                onCheckedChange = {
                    toggleCompletion(schedule.id, !schedule.isCompleted)
                    isCompleted = !isCompleted
                },
            )
        }
    }
}

private fun dateRange(
    schedule: ScheduleUiModel,
    today: LocalDateTime,
): String =
    when (schedule) {
        is AcademicScheduleUiModel -> formatDateRange(schedule.startAt, schedule.endAt)
        is PersonalScheduleUiModel -> remainingTimePersonal(today, schedule.endAt)
    }

private fun remainingTimePersonal(
    today: LocalDateTime,
    endAt: LocalDateTime,
): String {
    val duration = Duration.between(today, endAt)
    val totalHours = duration.toHours()
    val days = totalHours / 24
    val hours = totalHours % 24
    val minutes = duration.toMinutes() % 60
    return if (duration.isNegative) {
        "마감 지남"
    } else if (days > 0) {
        "${days}일 ${hours}시간 남음"
    } else if (hours > 0) {
        "${hours}시간 ${minutes}분 남음"
    } else {
        "${minutes}분 남음"
    }
}

private fun formatDateRange(
    startAt: LocalDate,
    endAt: LocalDate,
): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return "${startAt.format(formatter)} ~ ${endAt.format(formatter)}"
}

@Preview(showBackground = true)
@Composable
private fun ToDoScheduleItemPreview() {
    PlatoCalendarTheme {
        ToDoScheduleItem(
            schedule =
                CustomScheduleUiModel(
                    id = 1L,
                    title = "새해 계획 세우기",
                    description = "",
                    startAt = LocalDateTime.of(2024, 1, 11, 14, 0),
                    endAt = LocalDateTime.now().plusDays(2).plusHours(3),
                    isCompleted = false,
                ),
            today = LocalDateTime.now(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}
