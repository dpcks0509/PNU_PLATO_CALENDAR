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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.calendar.model.PickerTarget
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.util.component.dialog.schedule.content.ScheduleDialogContent
import pusan.university.plato_calendar.presentation.util.extension.formatTimeWithMidnightSpecialCase
import pusan.university.plato_calendar.presentation.util.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.util.saver.LocalDateSaver
import pusan.university.plato_calendar.presentation.util.saver.LocalDateTimeSaver
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.LightGray
import pusan.university.plato_calendar.presentation.util.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.util.theme.Red
import pusan.university.plato_calendar.presentation.util.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TITLE = "제목"
private const val HAS_NO_DESCRIPTION = "설명 없음"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomScheduleBottomSheet(
    schedule: CustomScheduleUiModel,
    editSchedule: (CustomSchedule) -> Unit,
    toggleScheduleCompletion: (Long, Boolean) -> Unit,
    deleteSchedule: () -> Unit,
    onShowDialog: (ScheduleDialogContent) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(schedule.title) }
    var description by rememberSaveable { mutableStateOf(schedule.description.orEmpty()) }
    var time by rememberSaveable(stateSaver = LocalDateTimeSaver) { mutableStateOf(schedule.endAt) }

    var timePickerFor by rememberSaveable { mutableStateOf<PickerTarget?>(null) }

    val zoneId = ZoneId.systemDefault()
    val today = LocalDateTime.now().toLocalDate()
    val currentMonthStart = rememberSaveable(today, saver = LocalDateSaver) { LocalDate.of(today.year, today.monthValue, 1) }
    val minDate = rememberSaveable(today, saver = LocalDateSaver) { minOf(today.minusDays(5), currentMonthStart) }
    val maxDate = rememberSaveable(today, saver = LocalDateSaver) { today.plusYears(1).minusDays(1) }

    fun initialMillisFor(dateTime: LocalDateTime): Long {
        val date = dateTime.toLocalDate()
        val clamped =
            when {
                date.isBefore(minDate) -> minDate
                date.isAfter(maxDate) -> maxDate
                else -> date
            }

        return clamped.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val dateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
    val formattedEndDate = remember(time) { time.format(dateFormatter) }
    val formattedEndTime = remember(time) { time.formatTimeWithMidnightSpecialCase() }
    val formattedEndYear = remember(time) { "${time.year}년" }

    val hasChanges =
        remember(title, description, time) {
            title.isNotEmpty() && (
                    title != schedule.title ||
                            description != schedule.description.orEmpty() ||
                            time != schedule.endAt
                    )
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
            text = "개인 일정",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )

        ActionButton(
            text = "수정",
            enabled = hasChanges,
            onClick = {
                editSchedule(
                    CustomSchedule(
                        id = schedule.id,
                        title = title,
                        description = description,
                        startAt = time,
                        endAt = time,
                        isCompleted = schedule.isCompleted,
                    ),
                )
            },
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
            value = title,
            onValueChange = { newValue ->
                val filteredValue = newValue.replace("\n", "")
                if (filteredValue.length <= 67) {
                    title = filteredValue
                }
            },
            placeholder = {
                Text(
                    text = TITLE,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = schedule.color,
                )
            },
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
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            TextField(
                value = description,
                onValueChange = { newValue ->
                    val filteredValue = newValue.replace("\n", "")
                    if (filteredValue.length <= 63) {
                        description = filteredValue
                    }
                },
                placeholder = {
                    Text(
                        text = HAS_NO_DESCRIPTION,
                        fontSize = 16.sp,
                        color = Gray,
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
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
                maxLines = 5,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGray)
                        .padding(vertical = 8.dp)
                        .noRippleClickable {
                            onShowDialog(
                                ScheduleDialogContent.DatePickerContent(
                                    initialSelectedDateMillis = initialMillisFor(time),
                                    minDateMillis = minDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    maxDateMillis = maxDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    onConfirm = { millis ->
                                        val pickedDate = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
                                        time = LocalDateTime.of(pickedDate, time.toLocalTime())
                                        timePickerFor = PickerTarget.END
                                    },
                                ),
                            )
                        },
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

            Spacer(modifier = Modifier.width(16.dp))
        }
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

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(36.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "삭제하기",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Red,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .noRippleClickable { deleteSchedule() },
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    timePickerFor?.let {
        LaunchedEffect(timePickerFor) {
            onShowDialog(
                ScheduleDialogContent.TimePickerContent(
                    initialHour = time.hour,
                    initialMinute = time.minute,
                    onConfirm = { hour, minute ->
                        time = time.withHour(hour).withMinute(minute)
                    },
                ),
            )

            timePickerFor = null
        }
    }
}
