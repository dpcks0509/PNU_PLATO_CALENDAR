package pusan.university.plato_calendar.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.common.component.AnimatedToast
import pusan.university.plato_calendar.presentation.common.eventbus.NotificationEvent
import pusan.university.plato_calendar.presentation.common.eventbus.NotificationEventBus
import pusan.university.plato_calendar.presentation.common.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.common.manager.LoadingManager
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.common.manager.SettingsManager
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarBottomBar
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarNavHost
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler.Companion.EXTRA_NOTIFICATION_ID
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler.Companion.EXTRA_SCHEDULE_ID
import pusan.university.plato_calendar.presentation.common.notification.NotificationHelper
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.White
import pusan.university.plato_calendar.presentation.widget.callback.OpenNewScheduleCallback
import javax.inject.Inject

@AndroidEntryPoint
class PlatoCalendarActivity : ComponentActivity() {
    @Inject
    lateinit var loginManager: LoginManager

    @Inject
    lateinit var scheduleManager: ScheduleManager

    @Inject
    lateinit var loadingManager: LoadingManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                lifecycleScope.launch {
                    settingsManager.setNotificationsEnabled(isGranted)
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        lifecycleScope.launch {
            loginManager.autoLogin()
            handleNotificationIntent(intent)
        }

        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.light(
                    scrim = Color.TRANSPARENT,
                    darkScrim = Color.TRANSPARENT,
                ),
            navigationBarStyle =
                SystemBarStyle.light(
                    scrim = Color.TRANSPARENT,
                    darkScrim = Color.TRANSPARENT,
                ),
        )

        setContent {
            val navController = rememberNavController()
            val isLoading by loadingManager.isLoading.collectAsStateWithLifecycle()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            PlatoCalendarTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        bottomBar = {
                            val isWebView = currentRoute?.contains("WebView") == true
                            if (!isWebView) {
                                PlatoCalendarBottomBar(navController = navController)
                            }
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        modifier = Modifier.fillMaxSize(),
                    ) { paddingValues ->
                        PlatoCalendarNavHost(
                            navController = navController,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .background(White)
                                    .padding(paddingValues),
                        )
                    }

                    AnimatedToast()

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
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        if (notificationId != -1) {
            notificationHelper.cancelNotification(notificationId)
        }
        if (scheduleId != -1L) {
            lifecycleScope.launch {
                NotificationEventBus.sendEvent(NotificationEvent.OpenSchedule(scheduleId))
            }
        }

        if (intent.action == OpenNewScheduleCallback.ACTION_OPEN_NEW_SCHEDULE) {
            lifecycleScope.launch {
                NotificationEventBus.sendEvent(NotificationEvent.OpenNewSchedule)
            }
        }
    }
}
