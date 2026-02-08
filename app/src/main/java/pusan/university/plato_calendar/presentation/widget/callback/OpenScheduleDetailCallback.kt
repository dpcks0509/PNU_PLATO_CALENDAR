package pusan.university.plato_calendar.presentation.widget.callback

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import pusan.university.plato_calendar.presentation.PlatoCalendarActivity
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler

class OpenScheduleDetailCallback : ActionCallback {
    companion object {
        const val EXTRA_SELECTED_DATE = "extra_selected_date"
        val scheduleIdKey = ActionParameters.Key<Long>("schedule_id")
        val selectedDateKey = ActionParameters.Key<String>("selected_date")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val scheduleId = parameters[scheduleIdKey] ?: return
        val selectedDate = parameters[selectedDateKey] ?: return

        val intent =
            Intent(context, PlatoCalendarActivity::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_SCHEDULE_ID, scheduleId)
                putExtra(EXTRA_SELECTED_DATE, selectedDate)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        context.startActivity(intent)
    }
}
