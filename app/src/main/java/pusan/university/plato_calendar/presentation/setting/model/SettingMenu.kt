package pusan.university.plato_calendar.presentation.setting.model

import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.ACCOUNT_INFO
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.ANNOUNCEMENTS
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.COLOR
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.CONTACT_US
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.FIRST_REMINDER
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.SECOND_REMINDER

enum class SettingMenu(
    val title: String,
    val items: List<SettingContent>,
) {
    ACCOUNT("계정", listOf(ACCOUNT_INFO)),
    NOTIFICATIONS(
        "알림",
        listOf(SettingContent.NOTIFICATIONS_ENABLED, FIRST_REMINDER, SECOND_REMINDER)
    ),
    THEME("테마", listOf(COLOR)),
    USER_SUPPORT("사용자 지원", listOf(ANNOUNCEMENTS, CONTACT_US));

    enum class SettingContent {
        ACCOUNT_INFO,
        NOTIFICATIONS_ENABLED,
        FIRST_REMINDER,
        SECOND_REMINDER,
        COLOR,
        ANNOUNCEMENTS,
        CONTACT_US,
        ;

        fun getLabel(): String =
            when (this) {
                ACCOUNT_INFO -> "계정 정보"
                COLOR -> "색상"
                NOTIFICATIONS_ENABLED -> "알림 허용하기"
                FIRST_REMINDER -> "알림"
                SECOND_REMINDER -> "두 번째 알림"
                ANNOUNCEMENTS -> "공지"
                CONTACT_US -> "문의하기"
            }

        fun getUrl(): String? =
            when (this) {
                ANNOUNCEMENTS -> "https://glaze-mustang-7cf.notion.site/28057846cad680089524ea45cb9afce1"
                CONTACT_US -> "https://open.kakao.com/o/ge5fZ0Uh"
                else -> null
            }
    }
}
