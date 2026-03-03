package pusan.university.plato_calendar.presentation.setting.model

import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.ACCOUNT_INFO
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.ANNOUNCEMENTS
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.AUTO_UPDATE_SCHEDULE
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.COLOR
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.CONTACT_US
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.FIRST_REMINDER
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.NOTIFICATIONS_ENABLED
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.PUSAN_NATIONAL_UNIVERSITY
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.SECOND_REMINDER
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.SMART_EDUCATION_PLATFORM
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.STUDENT_SUPPORT_SYSTEM

enum class SettingMenu(
    val title: String,
    val items: List<SettingContent>,
) {
    ACCOUNT(title = "계정", items = listOf(ACCOUNT_INFO)),
    NOTIFICATIONS(
        title = "알림",
        items =
            listOf(
                AUTO_UPDATE_SCHEDULE,
                NOTIFICATIONS_ENABLED,
                FIRST_REMINDER,
                SECOND_REMINDER,
            ),
    ),
    THEME(title = "테마", items = listOf(COLOR)),
    SHORT_CUT(
        title = "바로가기",
        items = listOf(PUSAN_NATIONAL_UNIVERSITY, STUDENT_SUPPORT_SYSTEM, SMART_EDUCATION_PLATFORM),
    ),
    USER_SUPPORT(title = "사용자 지원", items = listOf(ANNOUNCEMENTS, CONTACT_US)),
    ;

    enum class SettingContent(
        val label: String? = null,
        val description: String? = null,
        val url: String? = null,
    ) {
        ACCOUNT_INFO,
        AUTO_UPDATE_SCHEDULE(label = "일정 자동 업데이트"),
        NOTIFICATIONS_ENABLED(label = "알림 허용하기"),
        FIRST_REMINDER(label = "알림"),
        SECOND_REMINDER(label = "두 번째 알림"),
        COLOR,
        PUSAN_NATIONAL_UNIVERSITY(
            label = "부산대학교",
            url = "https://www.pusan.ac.kr/kor/Main.do",
        ),
        STUDENT_SUPPORT_SYSTEM(
            label = "학생지원시스템",
            url = "https://onestop.pusan.ac.kr",
        ),
        SMART_EDUCATION_PLATFORM(
            label = "스마트 교육 플랫폼 (PLATO)",
            url = "https://plato.pusan.ac.kr",
        ),
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
