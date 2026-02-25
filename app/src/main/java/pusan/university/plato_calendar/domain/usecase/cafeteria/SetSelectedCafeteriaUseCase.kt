package pusan.university.plato_calendar.domain.usecase.cafeteria

import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.repository.SelectedCafeteriaRepository
import javax.inject.Inject

class SetSelectedCafeteriaUseCase
    @Inject
    constructor(
        private val selectedCafeteriaRepository: SelectedCafeteriaRepository,
    ) {
        suspend operator fun invoke(cafeteria: Cafeteria) {
            selectedCafeteriaRepository.setSelectedCafeteria(cafeteria)
        }
    }
