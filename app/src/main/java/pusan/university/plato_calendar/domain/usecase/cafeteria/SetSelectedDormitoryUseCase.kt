package pusan.university.plato_calendar.domain.usecase.cafeteria

import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.repository.SelectedDormitoryRepository
import javax.inject.Inject

class SetSelectedDormitoryUseCase
@Inject
constructor(
    private val selectedDormitoryRepository: SelectedDormitoryRepository,
) {
    suspend operator fun invoke(dormitory: Dormitory) {
        selectedDormitoryRepository.setSelectedDormitory(dormitory)
    }
}
