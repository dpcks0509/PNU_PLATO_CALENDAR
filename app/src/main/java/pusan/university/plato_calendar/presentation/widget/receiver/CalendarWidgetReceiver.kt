package pusan.university.plato_calendar.presentation.widget.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.widget.CalendarWidget
import pusan.university.plato_calendar.presentation.widget.callback.RefreshSchedulesCallback

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        FirebaseAnalytics.getInstance(context).logEvent("widget_added", null)
        coroutineScope.launch {
            getSettingsManager(context).setAutoUpdateSchedule(true)
        }
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray,
    ) {
        super.onDeleted(context, appWidgetIds)

        FirebaseAnalytics.getInstance(context).logEvent("widget_deleted", null)
        coroutineScope.launch {
            getSettingsManager(context).setAutoUpdateSchedule(false)
        }
    }

    private fun getSettingsManager(context: Context) =
        EntryPointAccessors
            .fromApplication(
                context.applicationContext,
                CalendarWidget.WidgetEntryPoint::class.java,
            ).settingsManager()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        appWidgetIds.forEach { appWidgetId ->
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return@forEach

            coroutineScope.launch {
                try {
                    val glanceId =
                        GlanceAppWidgetManager(context)
                            .getGlanceIdBy(appWidgetId)

                    RefreshSchedulesCallback().onAction(
                        context = context,
                        glanceId = glanceId,
                        parameters = actionParametersOf(),
                    )
                } catch (_: IllegalArgumentException) {
                    // appWidgetId가 더 이상 유효하지 않음 (위젯이 제거됨)
                }
            }
        }
    }
}
