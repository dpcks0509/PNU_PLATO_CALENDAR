package pusan.university.plato_calendar.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.AppSettings
import pusan.university.plato_calendar.domain.repository.AppSettingsRepository
import javax.inject.Inject

class GetAppSettingsUseCase
    @Inject
    constructor(
        private val appSettingsRepository: AppSettingsRepository,
    ) {
        operator fun invoke(): Flow<AppSettings> = appSettingsRepository.getAppSettings()
    }
