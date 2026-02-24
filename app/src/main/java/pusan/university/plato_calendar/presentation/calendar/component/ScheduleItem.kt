package pusan.university.plato_calendar.presentation.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.util.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val HAS_NO_SCHEDULE = "일정 없음"

@Composable
fun ScheduleItem(
    schedules: List<ScheduleUiModel>,
    onScheduleClick: (ScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (schedules.isEmpty()) {
        Box(
            modifier =
                modifier
                    .padding(end = 44.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = HAS_NO_SCHEDULE,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray,
            )
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            schedules.forEach { schedule ->
                when (schedule) {
                    is AcademicScheduleUiModel -> {
                        AcademicScheduleItem(schedule = schedule, onScheduleClick = onScheduleClick)
                    }

                    is PersonalScheduleUiModel -> {
                        PersonalScheduleItem(schedule = schedule, onScheduleClick = onScheduleClick)
                    }
                }
            }
        }
    }
}

@Composable
fun AcademicScheduleItem(
    schedule: AcademicScheduleUiModel,
    onScheduleClick: (ScheduleUiModel) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(schedule.color)
                .noRippleClickable { onScheduleClick(schedule) }
                .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = schedule.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )

        Text(
            text = formatDateRange(schedule.startAt, schedule.endAt),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

@Composable
private fun PersonalScheduleItem(
    schedule: PersonalScheduleUiModel,
    onScheduleClick: (ScheduleUiModel) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(schedule.color)
                .noRippleClickable { onScheduleClick(schedule) }
                .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        val title =
            if (schedule is CourseScheduleUiModel) schedule.titleWithCourseName else schedule.title

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )

        Text(
            text = schedule.deadLine,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
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
fun ScheduleItemPreview() {
    PlatoCalendarTheme {
        val today = LocalDateTime.now().toLocalDate()
        val sampleSchedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "학사 일정",
                    startAt = today,
                    endAt = today,
                ),
                CourseScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "운영체제",
                    isCompleted = false,
                ),
                CourseScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "네트워크",
                    isCompleted = false,
                ),
                CustomScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    isCompleted = false,
                ),
                CustomScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    isCompleted = false,
                ),
            )

        ScheduleItem(
            schedules = sampleSchedules,
            onScheduleClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
