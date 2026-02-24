package pusan.university.plato_calendar.presentation.widget.callback

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import pusan.university.plato_calendar.presentation.widget.CalendarWidget
import java.time.LocalDate

class NavigateDateCallback : ActionCallback {
    companion object {
        val currentDateKey = ActionParameters.Key<String>("current_date")
        val offsetKey = ActionParameters.Key<String>("offset")
        val targetDateKey = ActionParameters.Key<String>("target_date")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val targetDateStr = parameters[targetDateKey]

        val newDate =
            if (targetDateStr != null) {
                LocalDate.parse(targetDateStr)
            } else {
                val currentDateStr = parameters[currentDateKey] ?: LocalDate.now().toString()
                val offsetStr = parameters[offsetKey] ?: "0"
                val currentDate = LocalDate.parse(currentDateStr)
                val offset = offsetStr.toLongOrNull() ?: 0L
                currentDate.plusDays(offset)
            }

        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[stringPreferencesKey("selected_date")] = newDate.toString()
        }

        CalendarWidget().update(context, glanceId)
    }
}
