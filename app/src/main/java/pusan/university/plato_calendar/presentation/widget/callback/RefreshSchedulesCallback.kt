package pusan.university.plato_calendar.presentation.widget.callback

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.serializer.PersonalScheduleSerializer
import pusan.university.plato_calendar.presentation.widget.CalendarWidget
import pusan.university.plato_calendar.presentation.widget.CalendarWidget.WidgetEntryPoint
import java.time.LocalDate

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

        val entryPoint =
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetEntryPoint::class.java,
            )

        val scheduleRepository = entryPoint.scheduleRepository()
        val courseRepository = entryPoint.courseRepository()
        val loginManager = entryPoint.loginManager()
        val settingsManager = entryPoint.settingsManager()
        val alarmScheduler = entryPoint.alarmScheduler()

        suspend fun getPersonalSchedules(sessKey: String): List<PersonalScheduleUiModel> {
            scheduleRepository
                .getPersonalSchedules(sessKey = sessKey)
                .onSuccess {
                    val personalSchedules =
                        it.map { domain ->
                            when (domain) {
                                is CourseSchedule -> {
                                    val courseName =
                                        courseRepository.getCourseName(
                                            domain.courseCode,
                                        )

                                    CourseScheduleUiModel(
                                        domain = domain,
                                        courseName = courseName,
                                    )
                                }

                                is CustomSchedule -> CustomScheduleUiModel(domain)
                            }
                        }

                    return personalSchedules
                }

            return emptyList()
        }

        suspend fun syncAlarms(schedules: List<ScheduleUiModel>) {
            val settings = settingsManager.appSettings.first()

            val personalSchedules =
                schedules
                    .filterIsInstance<PersonalScheduleUiModel>()
                    .filter { !it.isCompleted }

            if (settings.notificationsEnabled) {
                alarmScheduler.scheduleNotificationsForSchedule(
                    personalSchedules = personalSchedules,
                    firstReminderTime = settings.firstReminderTime,
                    secondReminderTime = settings.secondReminderTime,
                )
            }
        }

        loginManager.autoLogin()

        val personalSchedules =
            withContext(Dispatchers.IO) {
                when (val loginStatus = loginManager.loginStatus.value) {
                    is LoginStatus.Login -> {
                        getPersonalSchedules(loginStatus.loginSession.sessKey)
                    }

                    LoginStatus.Logout, LoginStatus.Uninitialized, LoginStatus.NetworkDisconnected, LoginStatus.LoginInProgress -> {
                        emptyList()
                    }
                }
            }

        syncAlarms(personalSchedules)

        val schedulesJson = PersonalScheduleSerializer.serializePersonalSchedules(personalSchedules)
        val today = LocalDate.now().toString()

        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[stringPreferencesKey("schedules_list")] = schedulesJson
            prefs[stringPreferencesKey("today")] = today
            prefs[stringPreferencesKey("selected_date")] = today
            prefs[booleanPreferencesKey("is_loading")] = false
        }

        CalendarWidget().update(context, glanceId)
    }
}
