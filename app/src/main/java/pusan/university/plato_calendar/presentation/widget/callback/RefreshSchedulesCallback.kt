package pusan.university.plato_calendar.presentation.widget.callback

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import pusan.university.plato_calendar.presentation.widget.CalendarWidget
import pusan.university.plato_calendar.presentation.widget.worker.RefreshSchedulesWorker

class RefreshSchedulesCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[booleanPreferencesKey("is_loading")] = true
        }
        CalendarWidget().update(context, glanceId)

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val workRequest =
            OneTimeWorkRequestBuilder<RefreshSchedulesWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
