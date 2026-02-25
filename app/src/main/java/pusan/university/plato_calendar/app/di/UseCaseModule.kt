package pusan.university.plato_calendar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.domain.repository.AppSettingsRepository
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import pusan.university.plato_calendar.domain.repository.CompletedScheduleRepository
import pusan.university.plato_calendar.domain.repository.CourseRepository
import pusan.university.plato_calendar.domain.repository.LoginCredentialsRepository
import pusan.university.plato_calendar.domain.repository.LoginRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import pusan.university.plato_calendar.domain.repository.SelectedCafeteriaRepository
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetCafeteriaWeeklyPlanUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetSelectedCafeteriaUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.SetSelectedCafeteriaUseCase
import pusan.university.plato_calendar.domain.usecase.course.GetCourseCodeUseCase
import pusan.university.plato_calendar.domain.usecase.course.GetCourseNameUseCase
import pusan.university.plato_calendar.domain.usecase.login.GetLoginCredentialsUseCase
import pusan.university.plato_calendar.domain.usecase.login.LoginUseCase
import pusan.university.plato_calendar.domain.usecase.login.LogoutUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.DeleteCustomScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.EditPersonalScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAcademicSchedulesUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetPersonalSchedulesUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.MakeCustomScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.MarkScheduleAsCompletedUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.MarkScheduleAsUncompletedUseCase
import pusan.university.plato_calendar.domain.usecase.settings.GetAppSettingsUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetNotificationsEnabledUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetReminderTimeUseCase
import pusan.university.plato_calendar.domain.usecase.settings.SetThemeModeUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // Course
    @Provides
    @Singleton
    fun provideGetCourseNameUseCase(courseRepository: CourseRepository): GetCourseNameUseCase =
        GetCourseNameUseCase(courseRepository)

    @Provides
    @Singleton
    fun provideGetCourseCodeUseCase(courseRepository: CourseRepository): GetCourseCodeUseCase =
        GetCourseCodeUseCase(courseRepository)

    // Login
    @Provides
    @Singleton
    fun provideLoginUseCase(
        loginRepository: LoginRepository,
        loginCredentialsRepository: LoginCredentialsRepository,
    ): LoginUseCase =
        LoginUseCase(loginRepository, loginCredentialsRepository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        loginRepository: LoginRepository,
        loginCredentialsRepository: LoginCredentialsRepository,
    ): LogoutUseCase =
        LogoutUseCase(loginRepository, loginCredentialsRepository)

    @Provides
    @Singleton
    fun provideGetLoginCredentialsUseCase(loginCredentialsRepository: LoginCredentialsRepository): GetLoginCredentialsUseCase =
        GetLoginCredentialsUseCase(loginCredentialsRepository)

    // Settings
    @Provides
    @Singleton
    fun provideGetAppSettingsUseCase(appSettingsRepository: AppSettingsRepository): GetAppSettingsUseCase =
        GetAppSettingsUseCase(appSettingsRepository)

    @Provides
    @Singleton
    fun provideSetNotificationsEnabledUseCase(appSettingsRepository: AppSettingsRepository): SetNotificationsEnabledUseCase =
        SetNotificationsEnabledUseCase(appSettingsRepository)

    @Provides
    @Singleton
    fun provideSetReminderTimeUseCase(appSettingsRepository: AppSettingsRepository): SetReminderTimeUseCase =
        SetReminderTimeUseCase(appSettingsRepository)

    @Provides
    @Singleton
    fun provideSetThemeModeUseCase(appSettingsRepository: AppSettingsRepository): SetThemeModeUseCase =
        SetThemeModeUseCase(appSettingsRepository)

    // Schedule
    @Provides
    @Singleton
    fun provideGetAcademicSchedulesUseCase(scheduleRepository: ScheduleRepository): GetAcademicSchedulesUseCase =
        GetAcademicSchedulesUseCase(scheduleRepository)

    @Provides
    @Singleton
    fun provideGetPersonalSchedulesUseCase(
        scheduleRepository: ScheduleRepository,
        completedScheduleRepository: CompletedScheduleRepository,
    ): GetPersonalSchedulesUseCase =
        GetPersonalSchedulesUseCase(scheduleRepository, completedScheduleRepository)

    @Provides
    @Singleton
    fun provideMakeCustomScheduleUseCase(scheduleRepository: ScheduleRepository): MakeCustomScheduleUseCase =
        MakeCustomScheduleUseCase(scheduleRepository)

    @Provides
    @Singleton
    fun provideEditPersonalScheduleUseCase(scheduleRepository: ScheduleRepository): EditPersonalScheduleUseCase =
        EditPersonalScheduleUseCase(scheduleRepository)

    @Provides
    @Singleton
    fun provideDeleteCustomScheduleUseCase(
        scheduleRepository: ScheduleRepository,
        completedScheduleRepository: CompletedScheduleRepository,
    ): DeleteCustomScheduleUseCase =
        DeleteCustomScheduleUseCase(scheduleRepository, completedScheduleRepository)

    @Provides
    @Singleton
    fun provideMarkScheduleAsCompletedUseCase(completedScheduleRepository: CompletedScheduleRepository): MarkScheduleAsCompletedUseCase =
        MarkScheduleAsCompletedUseCase(completedScheduleRepository)

    @Provides
    @Singleton
    fun provideMarkScheduleAsUncompletedUseCase(completedScheduleRepository: CompletedScheduleRepository): MarkScheduleAsUncompletedUseCase =
        MarkScheduleAsUncompletedUseCase(completedScheduleRepository)

    // Cafeteria
    @Provides
    @Singleton
    fun provideGetCafeteriaWeeklyPlanUseCase(cafeteriaRepository: CafeteriaRepository): GetCafeteriaWeeklyPlanUseCase =
        GetCafeteriaWeeklyPlanUseCase(cafeteriaRepository)

    @Provides
    @Singleton
    fun provideGetSelectedCafeteriaUseCase(selectedCafeteriaRepository: SelectedCafeteriaRepository): GetSelectedCafeteriaUseCase =
        GetSelectedCafeteriaUseCase(selectedCafeteriaRepository)

    @Provides
    @Singleton
    fun provideSetSelectedCafeteriaUseCase(selectedCafeteriaRepository: SelectedCafeteriaRepository): SetSelectedCafeteriaUseCase =
        SetSelectedCafeteriaUseCase(selectedCafeteriaRepository)
}
