package pusan.university.plato_calendar.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.data.local.database.CafeteriaDataStore
import pusan.university.plato_calendar.data.local.database.CompletedScheduleDataStore
import pusan.university.plato_calendar.data.local.database.LoginCredentialsDataStore
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
    fun provideCompletedScheduleDataStore(
        @ApplicationContext context: Context,
    ): CompletedScheduleDataStore = CompletedScheduleDataStore(context)

    @Provides
    @Singleton
    fun provideCafeteriaDataStore(
        @ApplicationContext context: Context,
    ): CafeteriaDataStore = CafeteriaDataStore(context)
}
