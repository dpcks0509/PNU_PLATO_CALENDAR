package pusan.university.plato_calendar.presentation.widget.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.extension.formatTimeWithMidnightSpecialCase
import pusan.university.plato_calendar.presentation.common.theme.BlackDark
import pusan.university.plato_calendar.presentation.common.theme.BlackLight
import pusan.university.plato_calendar.presentation.common.theme.GrayDark
import pusan.university.plato_calendar.presentation.common.theme.GrayLight
import pusan.university.plato_calendar.presentation.widget.CalendarWidget.ScheduleWidgetUiModel
import pusan.university.plato_calendar.presentation.widget.callback.OpenScheduleDetailCallback

@SuppressLint("RestrictedApi")
@Composable
fun ScheduleWidgetItem(schedule: PersonalScheduleUiModel) {
    val scheduleWidgetItem =
        when (schedule) {
            is CourseScheduleUiModel ->
                ScheduleWidgetUiModel(
                    schedule.titleWithCourseName,
                    schedule.endAt.formatTimeWithMidnightSpecialCase(),
                    if (schedule.isCompleted) {
                        R.drawable.widget_schedule_indicator_gray
                    } else {
                        R.drawable.widget_schedule_indicator_green
                    },
                )

            is CustomScheduleUiModel ->
                ScheduleWidgetUiModel(
                    schedule.title,
                    schedule.endAt.formatTimeWithMidnightSpecialCase(),
                    if (schedule.isCompleted) {
                        R.drawable.widget_schedule_indicator_gray
                    } else {
                        R.drawable.widget_schedule_indicator_red
                    },
                )
        }

    Row(
        modifier =
            GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(
                    actionRunCallback<OpenScheduleDetailCallback>(
                        actionParametersOf(
                            OpenScheduleDetailCallback.scheduleIdKey to schedule.id,
                            OpenScheduleDetailCallback.selectedDateKey to schedule.endAt.toLocalDate().toString(),
                        ),
                    ),
                ),
        verticalAlignment = Alignment.Vertical.CenterVertically,
    ) {
        Spacer(
            modifier =
                GlanceModifier
                    .width(8.dp)
                    .height(8.dp)
                    .background(ImageProvider(scheduleWidgetItem.indicatorDrawable)),
        )

        Spacer(modifier = GlanceModifier.width(12.dp))

        Column(
            modifier = GlanceModifier.defaultWeight(),
        ) {
            Text(
                text = scheduleWidgetItem.title,
                style =
                    TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(day = BlackLight, night = BlackDark)
                    ),
                maxLines = 2,
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = scheduleWidgetItem.deadLine,
                style =
                    TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = ColorProvider(day = GrayLight, night = GrayDark),
                    ),
            )
        }
    }
}
