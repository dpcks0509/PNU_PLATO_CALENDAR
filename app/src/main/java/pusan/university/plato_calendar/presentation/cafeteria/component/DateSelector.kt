package pusan.university.plato_calendar.presentation.cafeteria.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaWeeklyPlan
import pusan.university.plato_calendar.domain.entity.DailyCafeteriaPlan
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.LightGray
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateSelector(
    state: CafeteriaState,
    onEvent: (CafeteriaEvent) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        val weekStartDate = state.getWeekStartDate()
        val weekEndDate = state.getWeekEndDate()
        val canGoPrevious = weekStartDate?.let { !state.selectedDate.isEqual(it) } ?: false
        val canGoNext = weekEndDate?.let { !state.selectedDate.isEqual(it) } ?: false

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = { if (canGoPrevious) onEvent(CafeteriaEvent.PreviousDay) },
                        enabled = canGoPrevious,
                    ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = if (canGoPrevious) Gray else LightGray,
                modifier = Modifier.size(32.dp),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val dayOfWeek = state.selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
            val selectedDateInfo = "${state.selectedDate.monthValue.toString().padStart(2, '0')}월 ${
                state.selectedDate.dayOfMonth.toString().padStart(2, '0')
            }일 ($dayOfWeek)"

            Text(
                text = selectedDateInfo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = { if (canGoNext) onEvent(CafeteriaEvent.NextDay) },
                        enabled = canGoNext,
                    ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (canGoNext) Gray else LightGray,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DateSelectorPreview() {
    val today = LocalDate.now()
    val weekStart = today.minusDays(2)

    val dailyPlans = (0..6).map { dayOffset ->
        val date = weekStart.plusDays(dayOffset.toLong())
        DailyCafeteriaPlan(
            date = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
            day = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
            mealInfos = emptyList()
        )
    }

    val state = CafeteriaState(
        today = today,
        selectedDate = today,
        cafeteriaWeeklyPlans = mapOf(
            Cafeteria.GEUMJEONG_STUDENT to CafeteriaWeeklyPlan(
                cafeteria = Cafeteria.GEUMJEONG_STUDENT,
                notice = "",
                weeklyPlans = dailyPlans
            )
        ),
        selectedCafeteria = Cafeteria.GEUMJEONG_STUDENT
    )

    PlatoCalendarTheme {
        DateSelector(
            state = state,
            onEvent = {}
        )
    }
}
