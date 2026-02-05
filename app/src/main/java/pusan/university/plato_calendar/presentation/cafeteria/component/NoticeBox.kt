package pusan.university.plato_calendar.presentation.cafeteria.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.common.theme.Brown
import pusan.university.plato_calendar.presentation.common.theme.LightYellow
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.Yellow

@Composable
fun Notice(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LightYellow)
                .border(1.dp, Yellow, RoundedCornerShape(12.dp))
                .padding(16.dp),
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Brown,
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun NoticePreview() {
    PlatoCalendarTheme {
        Notice(
            text = "2월 6일(목) ~ 2월 9일(일)까지는 주말 및 공휴일로 운영하지 않습니다."
        )
    }
}
