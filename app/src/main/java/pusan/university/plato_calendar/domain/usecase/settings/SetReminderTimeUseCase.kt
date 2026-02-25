package pusan.university.plato_calendar.domain.usecase.settings

import pusan.university.plato_calendar.domain.repository.AppSettingsRepository
import pusan.university.plato_calendar.presentation.setting.model.NotificationTime
import javax.inject.Inject

class SetReminderTimeUseCase
    @Inject
    constructor(
        private val appSettingsRepository: AppSettingsRepository,
    ) {
        suspend operator fun invoke(
            firstTime: NotificationTime,
            secondTime: NotificationTime,
        ) {
            appSettingsRepository.setReminderTime(firstTime, secondTime)
        }
    }
