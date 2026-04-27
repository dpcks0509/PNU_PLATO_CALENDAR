package pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.domain.entity.AcademicScheduleAlarmInfo
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.setting.component.AcademicReminderDropdownItem
import pusan.university.plato_calendar.presentation.setting.component.ToggleSwitch
import pusan.university.plato_calendar.presentation.setting.model.AcademicNotificationHour
import pusan.university.plato_calendar.presentation.util.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.LightGray
import pusan.university.plato_calendar.presentation.util.theme.White
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AcademicScheduleBottomSheet(
    schedule: AcademicScheduleUiModel,
    alarmInfo: AcademicScheduleAlarmInfo? = null,
    onDismiss: () -> Unit,
    onAlarmUpdated: (Boolean, AcademicNotificationHour, AcademicNotificationHour) -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
    val formattedStartDate = schedule.startAt.format(dateFormatter)
    val formattedEndDate = schedule.endAt.format(dateFormatter)
    val formattedStartYear = "${schedule.startAt.year}년"
    val formattedEndYear = "${schedule.endAt.year}년"

    var notificationsEnabled by rememberSaveable { mutableStateOf(alarmInfo?.notificationsEnabled ?: false) }
    var startDateHour by rememberSaveable { mutableStateOf(alarmInfo?.startDateHour ?: AcademicNotificationHour.NONE) }
    var endDateHour by rememberSaveable { mutableStateOf(alarmInfo?.endDateHour ?: AcademicNotificationHour.NONE) }
    var isAlarmInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(notificationsEnabled, startDateHour, endDateHour) {
        if (isAlarmInitialized) {
            onAlarmUpdated(notificationsEnabled, startDateHour, endDateHour)
        } else {
            isAlarmInitialized = true
        }
    }

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
            contentDescription = null,
            tint = Color.White,
            modifier =
                Modifier
                    .size(32.dp)
                    .noRippleClickable(onClick = onDismiss),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "학사 일정",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
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
                )
                .background(White),
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
            textStyle = MaterialTheme.typography.titleLarge.copy(
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
                )
                .background(White)
                .padding(vertical = 18.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = null,
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
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
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
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ToggleSwitch(
            label = "알림 받기",
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(White)
                .padding(start = 4.dp, end = 8.dp)
        )

        AcademicReminderDropdownItem(
            label = "시작일",
            selectedLabel = startDateHour.label,
            enabled = notificationsEnabled,
            onSelect = { startDateHour = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(White)
                .padding(start = 4.dp, end = 8.dp)
        )

        AcademicReminderDropdownItem(
            label = "종료일",
            selectedLabel = endDateHour.label,
            enabled = notificationsEnabled,
            onSelect = { endDateHour = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(White)
                .padding(start = 4.dp, end = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
}
