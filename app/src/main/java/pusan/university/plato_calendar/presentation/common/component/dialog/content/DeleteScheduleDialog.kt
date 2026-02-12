package pusan.university.plato_calendar.presentation.common.component.dialog.content

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import pusan.university.plato_calendar.presentation.common.theme.Red

@Composable
fun DeleteScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "일정 삭제") },
        text = { Text(text = "일정을 삭제하시겠습니까?\n삭제된 일정은 복구할 수 없습니다.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "삭제", color = Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        },
    )
}
