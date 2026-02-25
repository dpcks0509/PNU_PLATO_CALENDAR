package pusan.university.plato_calendar.domain.usecase.course

import pusan.university.plato_calendar.domain.repository.CourseRepository
import javax.inject.Inject

class GetCourseCodeUseCase
    @Inject
    constructor(
        private val courseRepository: CourseRepository,
    ) {
        operator fun invoke(courseName: String): String = courseRepository.getCourseCode(courseName)
    }
