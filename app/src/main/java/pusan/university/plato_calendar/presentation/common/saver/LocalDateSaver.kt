package pusan.university.plato_calendar.presentation.common.saver

import androidx.compose.runtime.saveable.Saver
import java.time.LocalDate

val LocalDateSaver = Saver<LocalDate, String>(
    save = { it.toString() },
    restore = { LocalDate.parse(it) }
)
