package pusan.university.plato_calendar.presentation.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.VeryLightGray

private const val LOGIN_REQUIRED = "로그인이 필요합니다."

@Composable
fun Account(
    userInfo: String?,
    onClickLoginLogout: () -> Unit,
) {
    val isLoggedIn = userInfo != null
    val userName = if (isLoggedIn) userInfo else LOGIN_REQUIRED
    val buttonText = if (isLoggedIn) "로그아웃" else "로그인"

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(VeryLightGray)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(28.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = userName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier =
                Modifier
                    .width(76.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryColor)
                    .clickable { onClickLoginLogout() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = buttonText,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
