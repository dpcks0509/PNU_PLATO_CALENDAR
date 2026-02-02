package pusan.university.plato_calendar.presentation.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.common.theme.MediumGray
import pusan.university.plato_calendar.presentation.common.theme.VeryLightGray
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu

@Composable
fun SettingItem(
    content: SettingMenu.SettingContent,
    navigateToWebView: (String) -> Unit,
) {
    val text = content.getLabel()
    val url = content.getUrl()
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(VeryLightGray)
                .then(if (url != null) Modifier.clickable { navigateToWebView(url) } else Modifier)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MediumGray,
        )
    }
}
