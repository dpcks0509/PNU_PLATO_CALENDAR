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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.Gray
import pusan.university.plato_calendar.presentation.common.theme.LightGray

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
            val dayOfWeek =
                when (state.selectedDate.dayOfWeek.value) {
                    1 -> "월"
                    2 -> "화"
                    3 -> "수"
                    4 -> "목"
                    5 -> "금"
                    6 -> "토"
                    7 -> "일"
                    else -> ""
                }

            Text(
                text =
                    "${state.selectedDate.monthValue.toString().padStart(2, '0')}월 ${
                        state.selectedDate.dayOfMonth.toString().padStart(2, '0')
                    }일 ($dayOfWeek)",
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
