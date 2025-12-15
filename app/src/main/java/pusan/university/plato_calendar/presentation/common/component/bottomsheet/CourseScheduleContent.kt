package pusan.university.plato_calendar.presentation.common.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.common.extension.formatTimeWithMidnightSpecialCase
import pusan.university.plato_calendar.presentation.common.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.Gray
import pusan.university.plato_calendar.presentation.common.theme.LightGray
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.White
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val HAS_NO_DESCRIPTION = "설명 없음"

@Composable
fun CourseScheduleContent(
    schedule: CourseScheduleUiModel,
    toggleScheduleCompletion: (Long, Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
    val formattedStartDate = schedule.startAt.format(dateFormatter)
    val formattedStartTime = schedule.startAt.formatTimeWithMidnightSpecialCase()
    val formattedEndDate = schedule.endAt.format(dateFormatter)
    val formattedEndTime = schedule.endAt.formatTimeWithMidnightSpecialCase()
    val formattedStartYear = "${schedule.startAt.year}년"
    val formattedEndYear = "${schedule.endAt.year}년"

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(schedule.color)
                .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = White,
            modifier =
                Modifier
                    .size(32.dp)
                    .noRippleClickable(onDismissRequest),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "강의 일정",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = White,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier =
            Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true,
                    ambientColor = Black,
                    spotColor = Black,
                ).background(White),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(schedule.color),
        )

        TextField(
            value = schedule.title,
            readOnly = true,
            onValueChange = {},
            textStyle =
                TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                ),
            colors =
                TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = schedule.color,
                ),
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        modifier =
            Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true,
                    ambientColor = Black,
                    spotColor = Black,
                ).background(White)
                .padding(vertical = 18.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "CourseName",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            TextField(
                value = schedule.courseName,
                readOnly = true,
                onValueChange = { },
                textStyle =
                    TextStyle(
                        fontSize = 16.sp,
                        color = Black,
                    ),
                colors =
                    TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = schedule.color,
                    ),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Description",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            TextField(
                value = schedule.description.orEmpty(),
                readOnly = true,
                onValueChange = { },
                placeholder = {
                    Text(
                        text = HAS_NO_DESCRIPTION,
                        fontSize = 16.sp,
                        color = Gray,
                    )
                },
                textStyle =
                    TextStyle(
                        fontSize = 16.sp,
                        color = Black,
                    ),
                colors =
                    TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = schedule.color,
                    ),
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGray)
                        .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = formattedStartYear,
                    fontSize = 14.sp,
                    color = Gray,
                )
                Text(
                    text = formattedStartDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
                Text(
                    text = formattedStartTime,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Arrow",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGray)
                        .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = formattedEndYear,
                    fontSize = 14.sp,
                    color = Gray,
                )
                Text(
                    text = formattedEndDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
                Text(
                    text = formattedEndTime,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    Spacer(modifier = Modifier.height(12.dp))

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(36.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (schedule.isCompleted) "완료 해제" else "완료하기",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (schedule.isCompleted) Gray else PrimaryColor,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .noRippleClickable {
                        toggleScheduleCompletion(schedule.id, !schedule.isCompleted)
                    },
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
}
