package pusan.university.plato_calendar.domain.usecase.settings

import pusan.university.plato_calendar.domain.repository.AppSettingsRepository
import javax.inject.Inject

class SetNotificationsEnabledUseCase
    @Inject
    constructor(
        private val appSettingsRepository: AppSettingsRepository,
    ) {
        suspend operator fun invoke(enabled: Boolean) {
            appSettingsRepository.setNotificationsEnabled(enabled)
        }
    }
