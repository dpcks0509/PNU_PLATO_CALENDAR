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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.common.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.Gray
import pusan.university.plato_calendar.presentation.common.theme.LightGray
import pusan.university.plato_calendar.presentation.common.theme.White
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AcademicScheduleContent(
    schedule: AcademicScheduleUiModel,
    onDismissRequest: () -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
    val formattedStartDate = schedule.startAt.format(dateFormatter)
    val formattedEndDate = schedule.endAt.format(dateFormatter)
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
            contentDescription = null,
            tint = Color.White,
            modifier =
                Modifier
                    .size(32.dp)
                    .noRippleClickable(onClick = onDismissRequest),
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
    }

    Spacer(modifier = Modifier.height(12.dp))
}
