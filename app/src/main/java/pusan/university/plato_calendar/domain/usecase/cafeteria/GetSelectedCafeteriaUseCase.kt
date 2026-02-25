package pusan.university.plato_calendar.domain.usecase.cafeteria

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.repository.SelectedCafeteriaRepository
import javax.inject.Inject

class GetSelectedCafeteriaUseCase
    @Inject
    constructor(
        private val selectedCafeteriaRepository: SelectedCafeteriaRepository,
    ) {
        operator fun invoke(): Flow<Cafeteria> = selectedCafeteriaRepository.getSelectedCafeteria()
    }
