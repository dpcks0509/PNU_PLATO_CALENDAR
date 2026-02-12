package pusan.university.plato_calendar.presentation.common.component.bottomsheet.schedule.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, Color.White.copy(alpha = if (enabled) 1f else 0.5f)),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White.copy(alpha = 0.5f),
            ),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        modifier = Modifier.height(36.dp),
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}
