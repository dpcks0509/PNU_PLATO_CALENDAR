package pusan.university.plato_calendar.presentation.util.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSyncManager
    @Inject
    constructor(
        private val scheduleManager: ScheduleManager,
        private val settingsManager: SettingsManager,
        private val alarmScheduler: AlarmScheduler,
    ) {
        private var syncJob: Job? = null

        fun startSync(scope: CoroutineScope) {
            syncJob?.cancel()

            syncJob =
                scope.launch {
                    combine(
                        scheduleManager.schedules,
                        settingsManager.appSettings,
                    ) { schedules, settings ->
                        Pair(schedules, settings)
                    }.collect { (schedules, settings) ->
                        val personalSchedules =
                            schedules
                                .filterIsInstance<PersonalScheduleUiModel>()
                                .filter { !it.isCompleted }

                        if (!settings.notificationsEnabled) {
                            alarmScheduler.cancelAllNotifications(personalSchedules)
                            return@collect
                        }

                        personalSchedules.forEach { schedule ->
                            if (!schedule.notificationsEnabled) return@forEach

                            alarmScheduler.scheduleNotificationsForSchedule(
                                schedule = schedule,
                                firstReminderTime = if (schedule.isCustomized) schedule.firstReminderTime else settings.firstReminderTime,
                                secondReminderTime = if (schedule.isCustomized) schedule.secondReminderTime else settings.secondReminderTime,
                            )
                        }
                    }
                }
        }
    }
