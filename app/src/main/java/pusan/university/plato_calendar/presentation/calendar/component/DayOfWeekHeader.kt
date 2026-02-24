package pusan.university.plato_calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pusan.university.plato_calendar.presentation.calendar.model.DayOfWeekUiModel
import pusan.university.plato_calendar.presentation.calendar.model.DayOfWeekUiModel.Companion.dayOfWeeksEntries
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme

@Composable
fun DayOfWeekHeader(modifier: Modifier = Modifier) {
    val dayOfWeekEntries = dayOfWeeksEntries()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dayOfWeekEntries.forEach { dayOfWeek ->
            DayOfWeekItem(
                DayOfWeekUiModel.from(dayOfWeek),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayOfWeekHeaderPreview() {
    PlatoCalendarTheme {
        DayOfWeekHeader(
            modifier =
                Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
        )
    }
}
