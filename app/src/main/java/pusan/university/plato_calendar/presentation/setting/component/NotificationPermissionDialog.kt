package pusan.university.plato_calendar.presentation.setting.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun NotificationPermissionDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "알림 권한 필요") },
            text = { Text(text = "일정 알림을 받으려면 알림 권한이 필요합니다.\n설정에서 알림을 활성화해 주세요.") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = "설정으로 이동")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "취소")
                }
            },
        )
    }
}
