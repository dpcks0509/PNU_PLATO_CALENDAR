package pusan.university.plato_calendar.domain.usecase.settings

import pusan.university.plato_calendar.domain.repository.AppSettingsRepository
import pusan.university.plato_calendar.presentation.setting.model.ThemeMode
import javax.inject.Inject

class SetThemeModeUseCase
    @Inject
    constructor(
        private val appSettingsRepository: AppSettingsRepository,
    ) {
        suspend operator fun invoke(mode: ThemeMode) {
            appSettingsRepository.setThemeMode(mode)
        }
    }
