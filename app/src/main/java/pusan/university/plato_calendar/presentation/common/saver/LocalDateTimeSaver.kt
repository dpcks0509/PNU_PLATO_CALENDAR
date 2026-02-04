package pusan.university.plato_calendar.presentation.common.saver

import androidx.compose.runtime.saveable.Saver
import java.time.LocalDateTime

val LocalDateTimeSaver = Saver<LocalDateTime, String>(
    save = { it.toString() },
    restore = { LocalDateTime.parse(it) }
)