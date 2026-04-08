package pusan.university.plato_calendar.presentation.cafeteria.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.VeryLightGray

@Composable
fun DormitorySelector(
    selectedDormitory: Dormitory,
    onDormitorySelected: (Dormitory) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(vertical = 10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { isDropdownExpanded = true },
                )
                .padding(vertical = 4.dp),
        ) {
            Text(
                text = selectedDormitory.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(20.dp),
            )
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false },
            containerColor = VeryLightGray,
        ) {
            Dormitory.entries.forEach { dormitory ->
                DropdownMenuItem(
                    text = { Text(dormitory.title) },
                    onClick = {
                        onDormitorySelected(dormitory)
                        isDropdownExpanded = false
                    },
                )
            }
        }
    }
}
