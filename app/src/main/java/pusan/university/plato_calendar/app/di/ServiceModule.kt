package pusan.university.plato_calendar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.data.remote.service.AcademicScheduleService
import pusan.university.plato_calendar.data.remote.service.CafeteriaService
import pusan.university.plato_calendar.data.remote.service.LoginService
import pusan.university.plato_calendar.data.remote.service.PersonalScheduleService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideLoginService(
        @PlatoNonDirect retrofit: Retrofit,
    ): LoginService = retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    fun providePersonalScheduleService(
        @Plato retrofit: Retrofit,
    ): PersonalScheduleService = retrofit.create(PersonalScheduleService::class.java)

    @Provides
    @Singleton
    fun provideAcademicScheduleService(
        @PNU retrofit: Retrofit,
    ): AcademicScheduleService = retrofit.create(AcademicScheduleService::class.java)

    @Provides
    @Singleton
    fun provideCafeteriaService(
        @PNU retrofit: Retrofit,
    ): CafeteriaService = retrofit.create(CafeteriaService::class.java)
}
