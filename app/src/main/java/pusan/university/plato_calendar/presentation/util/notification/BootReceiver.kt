package pusan.university.plato_calendar.presentation.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.domain.usecase.schedule.GetAllScheduleAlarmInfosUseCase
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.util.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.util.manager.SettingsManager
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var scheduleManager: ScheduleManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var getAllScheduleAlarmInfosUseCase: GetAllScheduleAlarmInfosUseCase

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        val pendingResult = goAsync()
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        coroutineScope
            .launch {
                val settings = settingsManager.appSettings.first()
                val schedules = scheduleManager.schedules.first()
                val alarmInfos = getAllScheduleAlarmInfosUseCase()

                if (!settings.notificationsEnabled) return@launch

                val personalSchedules =
                    schedules.filterIsInstance<PersonalScheduleUiModel>().filter { !it.isCompleted }

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
            }.invokeOnCompletion {
                pendingResult.finish()
            }
    }
}
