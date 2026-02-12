package pusan.university.plato_calendar.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pusan.university.plato_calendar.presentation.common.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor

@Composable
fun LoadingIndicator(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .noRippleClickable(),
        ) {
            CircularProgressIndicator(
                color = PrimaryColor,
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .navigationBarsPadding(),
            )
        }
    }
}