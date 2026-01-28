package pusan.university.plato_calendar.domain.entity

enum class Cafeteria(
    val title: String,
    val campus: Campus,
    val buildingCode: String,
    val restaurantCode: String,
) {
    GEUMJEONG_STUDENT(
        title = "금정 학생",
        campus = Campus.PUSAN,
        buildingCode = "R001",
        restaurantCode = "PG002",
    ),
    GEUMJEONG_STAFF(
        title = "금정 교직",
        campus = Campus.PUSAN,
        buildingCode = "R001",
        restaurantCode = "PG001",
    ),
    MUNCHANG(
        title = "문창회관",
        campus = Campus.PUSAN,
        buildingCode = "R002",
        restaurantCode = "PM002",
    ),
    SAETBEOL(
        title = "샛벌회관",
        campus = Campus.PUSAN,
        buildingCode = "R003",
        restaurantCode = "PS001",
    ),
    PUSAN_STUDENT_HALL(
        title = "학생회관",
        campus = Campus.PUSAN,
        buildingCode = "R004",
        restaurantCode = "PH002",
    ),
    MIRYANG_STUDENT_HALL_STUDENT(
        title = "밀양 학생",
        campus = Campus.MIRYANG,
        buildingCode = "R005",
        restaurantCode = "M001",
    ),
    MIRYANG_STUDENT_HALL_STAFF(
        title = "밀양 교직",
        campus = Campus.MIRYANG,
        buildingCode = "R005",
        restaurantCode = "M002",
    ),
    YANGSAN_CONVENIENCE(
        title = "양산 편의",
        campus = Campus.YANGSAN,
        buildingCode = "R006",
        restaurantCode = "Y001",
    ),
}
