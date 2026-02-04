package pusan.university.plato_calendar.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshContainer(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            onRefresh()
            delay(1000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true },
        indicator = {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 8.dp),
            ) {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    color = PrimaryColor,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        },
    ) {
        content()
    }
}
