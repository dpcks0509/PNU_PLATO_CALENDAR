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
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.presentation.calendar.model.PickerTarget
import pusan.university.plato_calendar.presentation.util.component.dialog.schedule.content.ScheduleDialogContent
import pusan.university.plato_calendar.presentation.util.extension.formatTimeWithMidnightSpecialCase
import pusan.university.plato_calendar.presentation.util.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.util.saver.LocalDateSaver
import pusan.university.plato_calendar.presentation.util.saver.LocalDateTimeSaver
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.LightGray
import pusan.university.plato_calendar.presentation.util.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.util.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TITLE = "제목"
private const val DESCRIPTION = "설명"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScheduleBottomSheet(
    selectedDate: LocalDate,
    makeSchedule: (NewSchedule) -> Unit,
    onShowDialog: (ScheduleDialogContent) -> Unit,
    onDismiss: () -> Unit,
) {
    val color = PrimaryColor

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    var startAt by rememberSaveable(stateSaver = LocalDateTimeSaver) {
        val today = LocalDateTime.now()
        val initialStartTime = if (selectedDate == today.toLocalDate()) {
            today
        } else {
            LocalDateTime.of(selectedDate, LocalTime.of(9, 0))
        }
        mutableStateOf(initialStartTime)
    }
    var endAt by rememberSaveable(stateSaver = LocalDateTimeSaver) {
        mutableStateOf(startAt.plusHours(1))
    }
    var timePickerFor by rememberSaveable { mutableStateOf<PickerTarget?>(null) }

    val zoneId = ZoneId.systemDefault()
    val today = LocalDateTime.now().toLocalDate()
    val currentMonthStart = rememberSaveable(today, saver = LocalDateSaver) {
        LocalDate.of(
            today.year,
            today.monthValue,
            1
        )
    }
    val minDate = rememberSaveable(today, saver = LocalDateSaver) {
        minOf(
            today.minusDays(5),
            currentMonthStart
        )
    }
    val maxDate =
        rememberSaveable(today, saver = LocalDateSaver) { today.plusYears(1).minusDays(1) }

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
    val formattedStartDate = rememberSaveable(startAt) { startAt.format(dateFormatter) }
    val formattedStartTime =
        rememberSaveable(startAt) { startAt.formatTimeWithMidnightSpecialCase() }
    val formattedEndDate = rememberSaveable(endAt) { endAt.format(dateFormatter) }
    val formattedEndTime = rememberSaveable(endAt) { endAt.formatTimeWithMidnightSpecialCase() }
    val formattedStartYear = rememberSaveable(startAt) { "${startAt.year}년" }
    val formattedEndYear = rememberSaveable(endAt) { "${endAt.year}년" }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color)
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
            text = "일정 생성",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )

        ActionButton(
            text = "저장",
            enabled = title.isNotEmpty(),
            onClick = {
                makeSchedule(
                    NewSchedule(
                        title = title,
                        description = description,
                        startAt = startAt,
                        endAt = endAt,
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
                    .background(color),
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
                    color = color,
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
                    cursorColor = color,
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
                        text = DESCRIPTION,
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
                        cursorColor = color,
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

            Spacer(modifier = Modifier.width(8.dp))

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
                                    initialSelectedDateMillis = initialMillisFor(startAt),
                                    minDateMillis = minDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    maxDateMillis = maxDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    onConfirm = { millis ->
                                        val pickedDate = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
                                        startAt = LocalDateTime.of(pickedDate, startAt.toLocalTime())
                                        if (endAt.isBefore(startAt)) endAt = startAt.plusHours(1)
                                        timePickerFor = PickerTarget.START
                                    },
                                ),
                            )
                        },
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
                        .padding(vertical = 8.dp)
                        .noRippleClickable {
                            onShowDialog(
                                ScheduleDialogContent.DatePickerContent(
                                    initialSelectedDateMillis = initialMillisFor(endAt),
                                    minDateMillis = minDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    maxDateMillis = maxDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    onConfirm = { millis ->
                                        val pickedDate = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
                                        endAt = LocalDateTime.of(pickedDate, endAt.toLocalTime())
                                        if (endAt.isBefore(startAt)) startAt = endAt.minusHours(1)
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
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    timePickerFor?.let { target ->
        val initialDateTime = if (target == PickerTarget.START) startAt else endAt

        LaunchedEffect(timePickerFor) {
            onShowDialog(
                ScheduleDialogContent.TimePickerContent(
                    initialHour = initialDateTime.hour,
                    initialMinute = initialDateTime.minute,
                    onConfirm = { hour, minute ->
                        val updated =
                            initialDateTime
                                .withHour(hour)
                                .withMinute(minute)
                        if (target == PickerTarget.START) {
                            startAt = updated
                            if (endAt.isBefore(startAt)) endAt = startAt.plusHours(1)
                        } else {
                            endAt = updated
                            if (endAt.isBefore(startAt)) startAt = endAt.minusHours(1)
                        }
                    },
                ),
            )

            timePickerFor = null
        }
    }
}
