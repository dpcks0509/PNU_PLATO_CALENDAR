package pusan.university.plato_calendar.presentation.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.util.theme.VeryLightGray

@Composable
fun ThemeSelector(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VeryLightGray)
            .padding(4.dp),
    ) {
        ThemeMode.entries.forEach { mode ->
            val isSelected = mode == selectedTheme

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(40.dp))
                    .background(if (isSelected) PrimaryColor else Color.Transparent)
                    .clickable { onThemeSelected(mode) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(mode.iconRes),
                    contentDescription = mode.label,
                    tint = if (isSelected) Color.White else Black,
                    modifier = Modifier.size(16.dp),
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = mode.label,
                    color = if (isSelected) Color.White else Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
