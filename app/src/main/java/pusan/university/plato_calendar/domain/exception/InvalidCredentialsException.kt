package pusan.university.plato_calendar.domain.exception

class InvalidCredentialsException : Exception(INVALID_CREDENTIALS_ERROR) {
    companion object {
        private const val INVALID_CREDENTIALS_ERROR = "아이디 또는 패스워드가 잘못 입력되었습니다."
    }
}
