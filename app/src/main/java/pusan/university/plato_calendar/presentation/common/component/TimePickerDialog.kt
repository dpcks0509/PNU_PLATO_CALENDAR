package pusan.university.plato_calendar.presentation.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.White

@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
) {
    var selectedHour by rememberSaveable { mutableIntStateOf(initialHour) }
    var selectedMinute by rememberSaveable { mutableIntStateOf(initialMinute) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier
                        .background(White)
                        .padding(16.dp),
            ) {
                Text(
                    text = "시간 선택",
                    fontSize = 14.sp,
                    color = Black,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                TimePicker(
                    hour = selectedHour,
                    minute = selectedMinute,
                    onTimeChange = { hour, minute ->
                        selectedHour = hour
                        selectedMinute = minute
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "취소",
                            color = Black,
                            fontSize = 16.sp,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = { onConfirm(selectedHour, selectedMinute) }) {
                        Text(
                            text = "확인",
                            color = PrimaryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
