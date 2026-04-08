package pusan.university.plato_calendar.domain.usecase.cafeteria

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.repository.SelectedDormitoryRepository
import javax.inject.Inject

class GetSelectedDormitoryUseCase
@Inject
constructor(
    private val selectedDormitoryRepository: SelectedDormitoryRepository,
) {
    operator fun invoke(): Flow<Dormitory> = selectedDormitoryRepository.getSelectedDormitory()
}
