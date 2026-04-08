package pusan.university.plato_calendar.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pusan.university.plato_calendar.domain.entity.Dormitory
import java.io.IOException
import javax.inject.Inject

class DormitoryDataStore
@Inject
constructor(
    private val context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SELECTED_DORMITORY)

    val selectedDormitory: Flow<Dormitory> =
        context
            .dataStore
            .data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                val dormitoryName = preferences[KEY_SELECTED_DORMITORY_NAME]
                dormitoryName?.let {
                    runCatching { Dormitory.valueOf(it) }.getOrNull()
                } ?: Dormitory.JILLI
            }

    suspend fun setSelectedDormitory(dormitory: Dormitory) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SELECTED_DORMITORY_NAME] = dormitory.name
        }
    }

    companion object {
        private const val SELECTED_DORMITORY = "selected_dormitory"
        private val KEY_SELECTED_DORMITORY_NAME = stringPreferencesKey("selected_dormitory_name")
    }
}
