package pusan.university.plato_calendar.domain.usecase.course

import pusan.university.plato_calendar.domain.repository.CourseRepository
import javax.inject.Inject

class GetCourseNameUseCase
    @Inject
    constructor(
        private val courseRepository: CourseRepository,
    ) {
        operator fun invoke(courseCode: String): String = courseRepository.getCourseName(courseCode)
    }
