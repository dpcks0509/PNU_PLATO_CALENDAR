package pusan.university.plato_calendar.presentation.widget.callback

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import pusan.university.plato_calendar.presentation.main.MainActivity

class OpenNewScheduleCallback : ActionCallback {
    companion object {
        const val ACTION_OPEN_NEW_SCHEDULE = "OPEN_NEW_SCHEDULE"
        const val EXTRA_SELECTED_DATE = "extra_selected_date"
        val selectedDateKey = ActionParameters.Key<String>("selected_date")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val selectedDate = parameters[selectedDateKey] ?: return

        val intent =
            Intent(context, MainActivity::class.java).apply {
                action = ACTION_OPEN_NEW_SCHEDULE
                putExtra(EXTRA_SELECTED_DATE, selectedDate)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        context.startActivity(intent)
    }
}
