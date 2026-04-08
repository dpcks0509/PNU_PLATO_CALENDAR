package pusan.university.plato_calendar.domain.usecase.cafeteria

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.repository.SelectedCafeteriaRepository
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab
import javax.inject.Inject

class GetSelectedCafeteriaTabUseCase
    @Inject
    constructor(
        private val selectedCafeteriaRepository: SelectedCafeteriaRepository,
    ) {
        operator fun invoke(): Flow<CafeteriaTab> = selectedCafeteriaRepository.getSelectedTab()
    }
