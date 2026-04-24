package pusan.university.plato_calendar.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pusan.university.plato_calendar.data.local.database.AcademicScheduleAlarmDataStore
import pusan.university.plato_calendar.data.local.database.CafeteriaDataStore
import pusan.university.plato_calendar.data.local.database.DormitoryDataStore
import pusan.university.plato_calendar.data.local.database.LoginCredentialsDataStore
import pusan.university.plato_calendar.data.local.database.ScheduleAlarmDataStore
import pusan.university.plato_calendar.data.local.database.SettingsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideLoginCredentialsDataStore(
        @ApplicationContext context: Context,
    ): LoginCredentialsDataStore = LoginCredentialsDataStore(context)

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context,
    ): SettingsDataStore = SettingsDataStore(context)

    @Provides
    @Singleton
    fun provideCafeteriaDataStore(
        @ApplicationContext context: Context,
    ): CafeteriaDataStore = CafeteriaDataStore(context)

    @Provides
    @Singleton
    fun provideDormitoryDataStore(
        @ApplicationContext context: Context,
    ): DormitoryDataStore = DormitoryDataStore(context)

    @Provides
    @Singleton
    fun provideScheduleAlarmDataStore(
        @ApplicationContext context: Context,
    ): ScheduleAlarmDataStore = ScheduleAlarmDataStore(context)

    @Provides
    @Singleton
    fun provideAcademicScheduleAlarmDataStore(
        @ApplicationContext context: Context,
    ): AcademicScheduleAlarmDataStore = AcademicScheduleAlarmDataStore(context)

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}