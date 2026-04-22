# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# 컴파일 확인 (빠른 검증)
./gradlew :app:compileDebugKotlin

# 디버그 APK 빌드
./gradlew :app:assembleDebug

# 린트 (ktlint — 전 모듈 적용)
./gradlew :app:lintDebug
./gradlew ktlintCheck          # 코드 스타일 검사
./gradlew ktlintFormat         # 자동 포맷팅

# 전체 빌드
./gradlew build
```

단일 모듈 프로젝트(`:app`)이므로 모든 Gradle 태스크는 `:app:` 접두사를 사용한다. 테스트 디렉터리(`src/test/`, `src/androidTest/`)는 존재하나 현재 테스트 없음.

---

## 로컬 개발 환경 설정

빌드에 `local.properties` 필수. 다음 키를 설정해야 한다:

```properties
sdk.dir=/path/to/Android/sdk
plato.base.url="https://plato.pusan.ac.kr"
pnu.base.url="https://www.pusan.ac.kr"
storeFile=/path/to/keystore.jks
storePassword=...
keyPassword=...
keyAlias=...
banner.ad.sample.id=ca-app-pub-3940256099942544/6300978111   # 디버그용 샘플 AdMob ID
banner.ad.unit.id=...                                         # 릴리즈용 실제 AdMob ID
```

`plato.base.url`, `pnu.base.url`은 `BuildConfig` 필드로 주입된다. 릴리즈 빌드에는 서명 설정(`signingConfigs.release`)도 필요하다.

---

## 아키텍처 개요

**Clean Architecture + MVI** 패턴. 패키지 구조:

```
app/
├── app/          # Application, DI 모듈, 네트워크 설정
├── data/         # Repository 구현체, Retrofit 서비스, DataStore, 파서/크롤러
├── domain/       # 엔티티, Repository 인터페이스, UseCase
└── presentation/ # Compose 화면, ViewModel, UI 모델, 공유 매니저, 위젯, 알림
```

---

## MVI 패턴 (BaseViewModel)

모든 ViewModel은 `BaseViewModel<State, Event, SideEffect>`를 상속한다.

- **State** (`UiState`) — 화면 UI 상태. `setState { copy(...) }`로 업데이트. `WhileSubscribed(5000)` 타임아웃 적용
- **Event** (`UiEvent`) — 사용자 액션. `setEvent(event)`로 발행, `handleEvent()`에서 처리
- **SideEffect** (`UiSideEffect`) — 1회성 동작(네비게이션, BottomSheet 등). `setSideEffect { ... }`로 발행

각 화면마다 `intent/` 패키지에 `State`, `Event`, `SideEffect` 세 파일이 있다.

---

## 네트워크 계층

**Retrofit 인스턴스 3종** (Hilt qualifier로 구분):

| Qualifier | 용도 |
|---|---|
| `@Plato` (`@Redirect`) | PLATO LMS API, 리다이렉트 허용 |
| `@PlatoNonDirect` (`@NonDirect`) | PLATO API, 리다이렉트 차단 (로그인 응답 감지용) |
| `@PNU` | 부산대학교 학사일정/학식 API |

세션 쿠키는 `CookieJar`(JavaNetCookieJar)로 OkHttp가 자동 관리한다. 모든 요청에 `Referer: https://plato.pusan.ac.kr/`와 `User-Agent` 헤더가 추가된다. `NetworkConnectionInterceptor`가 네트워크 연결 상태를 사전 검사한다.

**API 결과 타입**: `ApiResult<T>` — `Success(data)` / `Error(exception)` sealed interface. Repository에서 반환하고 ViewModel에서 when 분기로 처리한다.

### 주요 API 엔드포인트

| 서비스 | 메서드 | 엔드포인트 | 설명 |
|---|---|---|---|
| `LoginService` | POST | `/login/index.php` | PLATO 로그인 (FormUrlEncoded) |
| `LoginService` | GET | `/login/logout.php` | 로그아웃 |
| `PersonalScheduleService` | POST | `/calendar/export.php` | ICS 형식 일정 내보내기 |
| `PersonalScheduleService` | POST | `/lib/ajax/service.php` | 커스텀 일정 CRUD (JSON) |
| `AcademicScheduleService` | POST | `/kor/CMS/Haksailjung/view.do` | 학사일정 HTML |
| `CafeteriaService` | POST | `/kor/CMS/MenuMgr/menuListOnBuilding.do` | 학식 메뉴 HTML |

---

## 데이터 파싱 계층

PLATO와 PNU API는 구조화된 JSON이 아닌 **ICS 및 HTML**을 반환한다. `data/util/parser/`에서 처리:

- **`ScheduleParser.parseIcsToPersonalSchedules()`** — RFC 5545 ICS 파싱. VEVENT 블록에서 UID, SUMMARY, DTSTART/DTEND 추출. `CATEGORIES`로 일정 타입(CourseSchedule/CustomSchedule) 분류. UTC→KST 변환
- **`ScheduleParser.parseHtmlToAcademicSchedules()`** — PNU 학사일정 HTML 테이블을 Jsoup으로 파싱. `class="term"`/`class="text"` 패턴 매칭
- **`CafeteriaParser.parseHtmlToWeeklyPlans()`** — 학식 HTML의 thead/tbody 구조 파싱. 조식/중식/석식 분류
- **`DormitoryCafeteriaParser`** — 기숙사 식단 파싱

**`DormitoryCafeteriaCrawler`** — 기숙사 식단은 동적 로딩이므로 WebView + JavaScript로 크롤링. `tbody#mealPlanTable` 폴링(500ms 간격, 30초 타임아웃)

---

## 로컬 저장소

Room 없음. **DataStore만** 사용:

- `LoginCredentialsDataStore` — PLATO 로그인 자격증명 저장
- `SettingsDataStore` — 앱 설정 (테마, 알림 시간 등)
- `CompletedScheduleDataStore` — 완료된 일정 ID 목록
- `CafeteriaDataStore` — 선택된 식당 정보
- `DormitoryDataStore` — 선택된 기숙사 식당 정보

---

## 공유 싱글턴 매니저

ViewModel 간 공유 상태는 Hilt `@Singleton` 매니저로 관리한다:

- **`LoginManager`** — 로그인 상태(`LoginStatus`) StateFlow 보유. `autoLogin()`, `login()`, `logout()` 제공
- **`ScheduleManager`** — 월별 캘린더 데이터 캐시, 오늘 날짜·선택 날짜 StateFlow 보유. 일정 업데이트 시 모든 월 캐시를 자동 갱신
- **`LoadingManager`** — 전역 로딩 상태

---

## 이벤트 버스

object 싱글턴으로 컴포넌트 간 단방향 이벤트 전달:

- `ToastEventBus` — 토스트 메시지 (`sendSuccess()`, `sendError()`)
- `WidgetEventBus` — 홈 위젯 갱신 트리거
- `DialogEventBus` — 다이얼로그 표시

---

## 네비게이션

`PlatoCalendarScreen` sealed interface에 4개 `@Serializable` data object 정의. Compose Navigation의 타입 안전 라우팅 사용 (문자열 경로 아님). `HorizontalPager` 기반 월 스크롤(캘린더), BottomBar 탭 전환 시 ordinal 기반 슬라이드 애니메이션 적용.

---

## 알림 시스템

- **`AlarmScheduler`** — `AlarmManager`로 일정별 최대 2개 리마인더 예약. 고유 알림 ID = scheduleId + reminderIndex
- **`NotificationHelper`** — `NotificationCompat`로 알림 생성. Android 13+ `POST_NOTIFICATIONS` 권한 체크
- **`AlarmReceiver`** — BroadcastReceiver. 예약 시간에 알림 표시
- **`BootReceiver`** — `BOOT_COMPLETED` 수신 후 알림 재등록

---

## 홈 위젯 (Glance)

`CalendarWidget`은 `GlanceAppWidget` 상속. Hilt EntryPoint로 싱글턴 접근. 향후 7일 일정 표시. `RefreshSchedulesCallback`, `NavigateDateCallback` 등 콜백으로 사용자 인터랙션 처리. `CalendarWidgetReceiver`가 브로드캐스트 수신.

---

## DI 모듈 구조

| 모듈 | 역할 |
|---|---|
| `NetworkModule` | OkHttpClient, Retrofit 인스턴스 3종, Json 설정, 인터셉터 |
| `DatabaseModule` | DataStore 인스턴스 5종 |
| `RepositoryModule` | domain 인터페이스 → data 구현체 `@Binds` 바인딩 |
| `ServiceModule` | Retrofit Service 인터페이스 제공 (qualifier별 Retrofit 주입) |

---

## 직렬화

`kotlinx.serialization` 사용. `Json` 설정: `ignoreUnknownKeys = true`, `coerceInputValues = true`, `encodeDefaults = true`.
