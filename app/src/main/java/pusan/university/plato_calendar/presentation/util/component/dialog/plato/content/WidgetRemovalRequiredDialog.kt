package pusan.university.plato_calendar.presentation.util.component.dialog.plato.content

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun WidgetRemovalRequiredDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "위젯 제거 필요") },
        text = { Text(text = "일정 자동 업데이트를 끄려면 홈 화면에서 위젯을 제거해 주세요. 위젯 제거 시, 1시간마다 최신 일정을 불러오는 기능이 중단됩니다.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "확인")
            }
        },
    )
}
