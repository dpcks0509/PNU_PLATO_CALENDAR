package pusan.university.plato_calendar.presentation.widget.worker

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.AcademicScheduleAlarmInfo
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.usecase.course.GetCourseNameUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAcademicSchedulesUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAllAcademicScheduleAlarmInfosUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAllScheduleAlarmInfosUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetPersonalSchedulesUseCase
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.setting.model.AcademicNotificationHour
import pusan.university.plato_calendar.presentation.util.manager.LoginManager
import pusan.university.plato_calendar.presentation.util.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.util.manager.SettingsManager
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler
import pusan.university.plato_calendar.presentation.util.serializer.PersonalScheduleSerializer
import pusan.university.plato_calendar.presentation.widget.CalendarWidget
import java.time.LocalDate

@HiltWorker
class RefreshSchedulesWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParams: WorkerParameters,
        private val loginManager: LoginManager,
        private val scheduleManager: ScheduleManager,
        private val settingsManager: SettingsManager,
        private val alarmScheduler: AlarmScheduler,
        private val getPersonalSchedulesUseCase: GetPersonalSchedulesUseCase,
        private val getCourseNameUseCase: GetCourseNameUseCase,
        private val getAllScheduleAlarmInfosUseCase: GetAllScheduleAlarmInfosUseCase,
        private val getAcademicSchedulesUseCase: GetAcademicSchedulesUseCase,
        private val getAllAcademicScheduleAlarmInfosUseCase: GetAllAcademicScheduleAlarmInfosUseCase,
    ) : CoroutineWorker(context, workerParams) {
        override suspend fun doWork(): Result {
            loginManager.autoLogin()

            val personalSchedules =
                withContext(Dispatchers.IO) {
                    when (val loginStatus = loginManager.loginStatus.value) {
                        is LoginStatus.Login -> getPersonalSchedules(loginStatus.loginSession.sessKey)
                        LoginStatus.NetworkDisconnected ->
                            scheduleManager.schedules.value.filterIsInstance<PersonalScheduleUiModel>()
                        LoginStatus.Logout, LoginStatus.Uninitialized, LoginStatus.LoginInProgress ->
                            emptyList()
                    }
                }

            syncAlarms(personalSchedules)

            val academicSchedules = getNotificationsEnabledAcademicSchedules()

            val schedulesJson = PersonalScheduleSerializer.serializePersonalSchedules(personalSchedules)
            val academicSchedulesJson = PersonalScheduleSerializer.serializeAcademicSchedules(academicSchedules)
            val today = LocalDate.now().toString()

            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(CalendarWidget::class.java)

            for (glanceId in glanceIds) {
                updateAppWidgetState(applicationContext, glanceId) { prefs ->
                    prefs[stringPreferencesKey("personal_schedules_list")] = schedulesJson
                    prefs[stringPreferencesKey("academic_schedules_list")] = academicSchedulesJson
                    prefs[stringPreferencesKey("today")] = today
                    prefs[stringPreferencesKey("selected_date")] = today
                    prefs[booleanPreferencesKey("is_loading")] = false
                }
            }

            for (glanceId in glanceIds) {
                CalendarWidget().update(applicationContext, glanceId)
            }

            return Result.success()
        }

        private suspend fun getPersonalSchedules(sessKey: String): List<PersonalScheduleUiModel> =
            when (val result = getPersonalSchedulesUseCase(sessKey)) {
                is ApiResult.Success -> {
                    result.data.map { domain ->
                        when (domain) {
                            is CourseSchedule -> {
                                val courseName = getCourseNameUseCase(domain.courseCode)
                                CourseScheduleUiModel(domain = domain, courseName = courseName)
                            }
                            is CustomSchedule -> CustomScheduleUiModel(domain)
                        }
                    }
                }
                is ApiResult.Error ->
                    scheduleManager.schedules.value.filterIsInstance<PersonalScheduleUiModel>()
            }

        private suspend fun syncAlarms(schedules: List<ScheduleUiModel>) {
            val settings = settingsManager.appSettings.first()
            if (!settings.notificationsEnabled) return

            val personalSchedules =
                schedules
                    .filterIsInstance<PersonalScheduleUiModel>()
                    .filter { !it.isCompleted }

            val alarmInfos = getAllScheduleAlarmInfosUseCase()

            personalSchedules.forEach { schedule ->
                val alarmInfo = alarmInfos[schedule.id]
                val enabled = alarmInfo?.notificationsEnabled ?: true
                if (!enabled) return@forEach

                alarmScheduler.scheduleNotificationsForSchedule(
                    schedule = schedule,
                    firstReminderTime = if (alarmInfo?.isCustomized == true) alarmInfo.firstReminderTime else settings.firstReminderTime,
                    secondReminderTime = if (alarmInfo?.isCustomized == true) alarmInfo.secondReminderTime else settings.secondReminderTime,
                )
            }
        }

        private suspend fun getNotificationsEnabledAcademicSchedules(): List<AcademicScheduleUiModel> {
            val academicSchedules =
                when (val result = getAcademicSchedulesUseCase()) {
                    is ApiResult.Success -> result.data.map { AcademicScheduleUiModel(it) }
                    is ApiResult.Error -> emptyList()
                }

            val alarmInfos = getAllAcademicScheduleAlarmInfosUseCase()
            val alarmMap = alarmInfos.associateBy {
                AcademicScheduleAlarmInfo.generateKey(it.title, it.startAt, it.endAt)
            }

            return academicSchedules.mapNotNull { schedule ->
                val key = AcademicScheduleAlarmInfo.generateKey(schedule.title, schedule.startAt, schedule.endAt)
                val info = alarmMap[key] ?: return@mapNotNull null
                if (!info.notificationsEnabled ||
                    (info.startDateHour == AcademicNotificationHour.NONE && info.endDateHour == AcademicNotificationHour.NONE)
                ) return@mapNotNull null

                alarmScheduler.scheduleAcademicNotification(
                    title = schedule.title,
                    startAt = schedule.startAt,
                    endAt = schedule.endAt,
                    startDateHour = info.startDateHour,
                    endDateHour = info.endDateHour,
                )

                schedule.copy(id = info.notificationBaseId?.let { -(it.toLong()) } ?: 0L)
            }
        }
    }
