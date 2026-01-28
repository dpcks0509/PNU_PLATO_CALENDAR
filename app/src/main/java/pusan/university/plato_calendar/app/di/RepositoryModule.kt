package pusan.university.plato_calendar.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.data.local.repository.LocalCourseRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteCafeteriaRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteLoginRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteScheduleRepository
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import pusan.university.plato_calendar.domain.repository.CourseRepository
import pusan.university.plato_calendar.domain.repository.LoginRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLoginRepository(repositoryImpl: RemoteLoginRepository): LoginRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(repositoryImpl: LocalCourseRepository): CourseRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(repositoryImpl: RemoteScheduleRepository): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindCafeteriaRepository(repositoryImpl: RemoteCafeteriaRepository): CafeteriaRepository
}
