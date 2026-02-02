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
import pusan.university.plato_calendar.domain.entity.Cafeteria
import java.io.IOException
import javax.inject.Inject

class CafeteriaDataStore
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SELECTED_CAFETERIA)

        val selectedCafeteria: Flow<Cafeteria> =
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
                    val cafeteriaName = preferences[KEY_SELECTED_CAFETERIA_NAME]
                    cafeteriaName?.let {
                        runCatching { Cafeteria.valueOf(it) }.getOrNull()
                    } ?: Cafeteria.GEUMJEONG_STUDENT
                }

        suspend fun setSelectedCafeteria(cafeteria: Cafeteria) {
            context.dataStore.edit { prefs ->
                prefs[KEY_SELECTED_CAFETERIA_NAME] = cafeteria.name
            }
        }

        suspend fun clearSelectedCafeteria() {
            context.dataStore.edit { prefs ->
                prefs.remove(KEY_SELECTED_CAFETERIA_NAME)
            }
        }

        companion object {
            private const val SELECTED_CAFETERIA = "selected_cafeteria"
            private val KEY_SELECTED_CAFETERIA_NAME = stringPreferencesKey("selected_cafeteria_name")
        }
    }
