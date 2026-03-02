package pusan.university.plato_calendar.domain.usecase.settings

import pusan.university.plato_calendar.domain.repository.AppSettingsRepository
import javax.inject.Inject

class SetAutoUpdateScheduleUseCase
    @Inject
    constructor(
        private val appSettingsRepository: AppSettingsRepository,
    ) {
        suspend operator fun invoke(enabled: Boolean) {
            appSettingsRepository.setAutoUpdateSchedule(enabled)
        }
    }
