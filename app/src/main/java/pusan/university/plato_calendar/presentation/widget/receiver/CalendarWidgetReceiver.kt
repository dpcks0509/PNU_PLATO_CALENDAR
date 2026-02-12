package pusan.university.plato_calendar.presentation.widget.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.widget.CalendarWidget
import pusan.university.plato_calendar.presentation.widget.callback.RefreshSchedulesCallback

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = CalendarWidget

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        coroutineScope.launch {
            appWidgetIds.forEach { appWidgetId ->
                try {
                    val glanceId =
                        GlanceAppWidgetManager(context)
                            .getGlanceIdBy(appWidgetId)

                    RefreshSchedulesCallback().onAction(
                        context = context,
                        glanceId = glanceId,
                        parameters = actionParametersOf(),
                    )
                } catch (_: Exception) {
                }
            }
        }
    }
}
