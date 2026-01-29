package pusan.university.plato_calendar.presentation.common.icon

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class IconType {
    data class Vector(
        val imageVector: ImageVector,
    ) : IconType()

    data class Resource(
        @DrawableRes val resId: Int,
    ) : IconType()
}
