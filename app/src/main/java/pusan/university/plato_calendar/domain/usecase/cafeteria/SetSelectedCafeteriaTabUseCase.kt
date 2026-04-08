package pusan.university.plato_calendar.domain.usecase.cafeteria

import pusan.university.plato_calendar.domain.repository.SelectedCafeteriaRepository
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab
import javax.inject.Inject

class SetSelectedCafeteriaTabUseCase
    @Inject
    constructor(
        private val selectedCafeteriaRepository: SelectedCafeteriaRepository,
    ) {
        suspend operator fun invoke(tab: CafeteriaTab) {
            selectedCafeteriaRepository.setSelectedTab(tab)
        }
    }
