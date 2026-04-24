package pusan.university.plato_calendar.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.data.local.repository.LocalAcademicScheduleAlarmRepository
import pusan.university.plato_calendar.data.local.repository.LocalAppSettingsRepository
import pusan.university.plato_calendar.data.local.repository.LocalCourseRepository
import pusan.university.plato_calendar.data.local.repository.LocalLoginCredentialsRepository
import pusan.university.plato_calendar.data.local.repository.LocalScheduleAlarmRepository
import pusan.university.plato_calendar.data.local.repository.LocalSelectedCafeteriaRepository
import pusan.university.plato_calendar.data.local.repository.LocalSelectedDormitoryRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteCafeteriaRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteDormitoryCafeteriaRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteLoginRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteScheduleRepository
import pusan.university.plato_calendar.domain.repository.AcademicScheduleAlarmRepository
import pusan.university.plato_calendar.domain.repository.AppSettingsRepository
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import pusan.university.plato_calendar.domain.repository.CourseRepository
import pusan.university.plato_calendar.domain.repository.DormitoryCafeteriaRepository
import pusan.university.plato_calendar.domain.repository.LoginCredentialsRepository
import pusan.university.plato_calendar.domain.repository.LoginRepository
import pusan.university.plato_calendar.domain.repository.ScheduleAlarmRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import pusan.university.plato_calendar.domain.repository.SelectedCafeteriaRepository
import pusan.university.plato_calendar.domain.repository.SelectedDormitoryRepository
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

    @Binds
    @Singleton
    abstract fun bindSelectedCafeteriaRepository(repositoryImpl: LocalSelectedCafeteriaRepository): SelectedCafeteriaRepository

    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(repositoryImpl: LocalAppSettingsRepository): AppSettingsRepository

    @Binds
    @Singleton
    abstract fun bindLoginCredentialsRepository(repositoryImpl: LocalLoginCredentialsRepository): LoginCredentialsRepository

    @Binds
    @Singleton
    abstract fun bindDormitoryCafeteriaRepository(repositoryImpl: RemoteDormitoryCafeteriaRepository): DormitoryCafeteriaRepository

    @Binds
    @Singleton
    abstract fun bindSelectedDormitoryRepository(repositoryImpl: LocalSelectedDormitoryRepository): SelectedDormitoryRepository

    @Binds
    @Singleton
    abstract fun bindScheduleAlarmRepository(repositoryImpl: LocalScheduleAlarmRepository): ScheduleAlarmRepository

    @Binds
    @Singleton
    abstract fun bindAcademicScheduleAlarmRepository(repositoryImpl: LocalAcademicScheduleAlarmRepository): AcademicScheduleAlarmRepository
}
