package pusan.university.plato_calendar.presentation.calendar.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.calendar.model.DayOfWeekUiModel
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import java.time.DayOfWeek

@Composable
fun DayOfWeekItem(
    dayOfWeek: DayOfWeekUiModel,
    modifier: Modifier = Modifier,
) {
    Text(
        text = dayOfWeek.title,
        color = dayOfWeek.color,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun DayOfWeekItemPreview() {
    PlatoCalendarTheme {
        DayOfWeekItem(
            DayOfWeekUiModel.from(DayOfWeek.SATURDAY),
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}