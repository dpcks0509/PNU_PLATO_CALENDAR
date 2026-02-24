package pusan.university.plato_calendar.presentation.cafeteria.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.presentation.util.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.util.theme.Brown
import pusan.university.plato_calendar.presentation.util.theme.LightYellow
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.util.theme.Yellow

@Composable
fun Notice(
    text: String,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LightYellow)
                .border(1.dp, Yellow, RoundedCornerShape(12.dp))
                .animateContentSize(animationSpec = tween(durationMillis = 300))
                .noRippleClickable { isExpanded = !isExpanded },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notice),
                contentDescription = null,
                tint = Brown,
                modifier = Modifier.padding(top = 2.dp, end = 6.dp),
            )

            Text(
                text = text,
                fontSize = 14.sp,
                color = Brown,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Brown,
                modifier =
                    Modifier
                        .padding(start = 2.dp)
                        .rotate(rotation),
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun NoticePreview() {
    PlatoCalendarTheme {
        Notice(
            text = "2월 6일(목) ~ 2월 9일(일)까지는 주말 및 공휴일로 운영하지 않습니다.",
        )
    }
}
