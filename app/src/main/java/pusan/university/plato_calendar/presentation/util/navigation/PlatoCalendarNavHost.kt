package pusan.university.plato_calendar.presentation.util.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import pusan.university.plato_calendar.presentation.cafeteria.CafeteriaScreen
import pusan.university.plato_calendar.presentation.calendar.CalendarScreen
import pusan.university.plato_calendar.presentation.setting.SettingScreen
import pusan.university.plato_calendar.presentation.todo.ToDoScreen
import pusan.university.plato_calendar.presentation.util.component.WebView
import pusan.university.plato_calendar.presentation.util.navigation.PlatoCalendarScreen.CafeteriaScreen
import pusan.university.plato_calendar.presentation.util.navigation.PlatoCalendarScreen.CalendarScreen
import pusan.university.plato_calendar.presentation.util.navigation.PlatoCalendarScreen.SettingScreen
import pusan.university.plato_calendar.presentation.util.navigation.PlatoCalendarScreen.ToDoScreen
import pusan.university.plato_calendar.presentation.util.navigation.PlatoCalendarScreen.WebViewScreen

@Composable
fun PlatoCalendarNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = CalendarScreen,
        modifier = modifier,
    ) {
        composable<CalendarScreen>(
            enterTransition = { ordinalSlideEnter() },
            exitTransition = { ordinalSlideExit() },
            popEnterTransition = { ordinalSlideEnter() },
            popExitTransition = { ordinalSlideExit() },
        ) {
            CalendarScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<ToDoScreen>(
            enterTransition = { ordinalSlideEnter() },
            exitTransition = { ordinalSlideExit() },
            popEnterTransition = { ordinalSlideEnter() },
            popExitTransition = { ordinalSlideExit() },
        ) {
            ToDoScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<CafeteriaScreen>(
            enterTransition = { ordinalSlideEnter() },
            exitTransition = { ordinalSlideExit() },
            popEnterTransition = { ordinalSlideEnter() },
            popExitTransition = { ordinalSlideExit() },
        ) {
            CafeteriaScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<SettingScreen>(
            enterTransition = { ordinalSlideEnter() },
            exitTransition = { ordinalSlideExit() },
            popEnterTransition = { ordinalSlideEnter() },
            popExitTransition = { ordinalSlideExit() },
        ) {
            SettingScreen(
                navigateToWebView = { url -> navController.navigate(WebViewScreen(url)) },
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<WebViewScreen> {
            val url = it.toRoute<WebViewScreen>().url

            WebView(
                url = url,
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            )
        }
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.computeOrdinalSlideDirection():
    AnimatedContentTransitionScope.SlideDirection? {
    val fromIndex =
        BottomBarItem.entries.indexOfFirst { it.route::class.qualifiedName == initialState.destination.route }
    val toIndex =
        BottomBarItem.entries.indexOfFirst { it.route::class.qualifiedName == targetState.destination.route }
    if (fromIndex == -1 || toIndex == -1) return null
    return if (fromIndex < toIndex) {
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.ordinalSlideEnter(): EnterTransition? {
    val direction = computeOrdinalSlideDirection() ?: return null
    return slideIntoContainer(direction, animationSpec = tween())
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.ordinalSlideExit(): ExitTransition? {
    val direction = computeOrdinalSlideDirection() ?: return null
    return slideOutOfContainer(direction, animationSpec = tween())
}
