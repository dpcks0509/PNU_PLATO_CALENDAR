package pusan.university.plato_calendar.presentation.util.component.dialog.plato.content

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun WidgetRequiredDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "위젯 등록 필요") },
        text = { Text(text = "일정 자동 업데이트를 사용하려면 홈 화면에 위젯을 추가해 주세요. 위젯 추가 시, 1시간마다 최신 일정을 불러올 수 있어요.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "확인")
            }
        },
    )
}
