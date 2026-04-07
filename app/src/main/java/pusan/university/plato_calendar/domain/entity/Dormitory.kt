package pusan.university.plato_calendar.domain.entity

enum class Dormitory(val siteId: String, val campusId: String, val tabId: String?, val title: String) {
    JILLI(siteId = "pdorm", campusId = "000000000000561", tabId = "pDormTab1", title = "부산 진리관"),
    UNGBEE(siteId = "pdorm", campusId = "000000000000561", tabId = "pDormTab2", title = "부산 웅비관"),
    JAYU(siteId = "pdorm", campusId = "000000000000561", tabId = "pDormTab3", title = "부산 자유관"),
    MILYANG(siteId = "mdorm", campusId = "000000000000628", tabId = null, title = "밀양 기숙사"),
    YANGSAN(siteId = "ydorm", campusId = "000000000000596", tabId = null, title = "양산 기숙사")
}