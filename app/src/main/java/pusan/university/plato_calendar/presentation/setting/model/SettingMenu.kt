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
        listOf(SettingContent.NOTIFICATIONS_ENABLED, FIRST_REMINDER, SECOND_REMINDER),
    ),
    THEME("테마", listOf(COLOR)),
    STUDENT_SUPPORT("학생지원", listOf(ANNOUNCEMENTS, CONTACT_US)),
    ;

    enum class SettingContent(
        val label: String? = null,
        val description: String? = null,
        val url: String? = null,
    ) {
        ACCOUNT_INFO,
        NOTIFICATIONS_ENABLED(label = "알림 허용하기"),
        FIRST_REMINDER(label = "알림"),
        SECOND_REMINDER(label = "두 번째 알림"),
        COLOR,
        ANNOUNCEMENTS(
            label = "공지사항",
            url = "https://glaze-mustang-7cf.notion.site/28057846cad680089524ea45cb9afce1",
        ),
        CONTACT_US(
            label = "의견 남기기",
            url = "https://open.kakao.com/o/ge5fZ0Uh",
        ),
    }
}
