package pusan.university.plato_calendar.presentation.common.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadingManager
    @Inject
    constructor() {
        private val _isLoading = MutableStateFlow(true)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        fun updateLoading(isLoading: Boolean) {
            _isLoading.value = isLoading
        }
    }
