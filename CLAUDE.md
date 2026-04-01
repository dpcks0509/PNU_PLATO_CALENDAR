# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# 컴파일 확인 (빠른 검증)
./gradlew :app:compileDebugKotlin

# 디버그 APK 빌드
./gradlew :app:assembleDebug

# 린트
./gradlew :app:lintDebug

# 전체 빌드
./gradlew build
```

단일 모듈 프로젝트(`:app`)이므로 모든 Gradle 태스크는 `:app:` 접두사를 사용한다.

---

## 아키텍처 개요

**Clean Architecture + MVI** 패턴. 패키지 구조:

```
app/
├── app/          # Application, DI 모듈, 네트워크 설정
├── data/         # Repository 구현체 (remote/local), Retrofit 서비스, DataStore
├── domain/       # 엔티티, Repository 인터페이스, UseCase
└── presentation/ # Compose 화면, ViewModel, UI 모델, 공유 매니저
```

---

## MVI 패턴 (BaseViewModel)

모든 ViewModel은 `BaseViewModel<State, Event, SideEffect>`를 상속한다.

- **State** (`UiState`) — 화면 UI 상태. `setState { copy(...) }`로 업데이트
- **Event** (`UiEvent`) — 사용자 액션. `setEvent(event)`로 발행, `handleEvent()`에서 처리
- **SideEffect** (`UiSideEffect`) — 1회성 동작(네비게이션, BottomSheet 표시 등). `setSideEffect { ... }`로 발행

각 화면마다 `intent/` 패키지에 `State`, `Event`, `SideEffect` 세 파일이 있다.

---

## 네트워크 계층

**Retrofit 인스턴스 3종** (Hilt qualifier로 구분):

| Qualifier | 용도 |
|---|---|
| `@Plato` | PLATO LMS API, 리다이렉트 허용 |
| `@PlatoNonDirect` | PLATO API, 리다이렉트 차단 (로그인 응답 감지용) |
| `@PNU` | 부산대학교 학사일정 API |

세션 쿠키는 `CookieJar`(JavaNetCookieJar)로 OkHttp가 자동 관리한다. 모든 요청에 `Referer: https://plato.pusan.ac.kr/`와 `User-Agent` 헤더가 추가된다.

**API 결과 타입**: `ApiResult<T>` — `Success(data)` / `Error(exception)` sealed interface. Repository에서 반환하고 ViewModel에서 when 분기로 처리한다.

---

## 로컬 저장소

Room 없음. **DataStore만** 사용:

- `LoginCredentialsDataStore` — PLATO 로그인 자격증명 저장
- `SettingsDataStore` — 앱 설정 (테마, 알림 시간 등)
- `CompletedScheduleDataStore` — 완료된 일정 ID 목록
- `CafeteriaDataStore` — 선택된 식당 정보

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

## DI 모듈 구조

| 모듈 | 역할 |
|---|---|
| `NetworkModule` | OkHttpClient, Retrofit 인스턴스, Json 설정 |
| `DatabaseModule` | DataStore 인스턴스 |
| `RepositoryModule` | domain 인터페이스 → data 구현체 바인딩 |
| `ServiceModule` | Retrofit Service 인터페이스 제공 |

---

## 화면 구성

4개 탭 (BottomBar): **캘린더**, **투두**, **학식**, **설정**

- 캘린더: 학사일정(PNU API) + PLATO 개인일정 통합 표시. HorizontalPager로 월 스크롤
- 투두: 완료/미완료 일정 목록
- 학식: 교내 식당 메뉴 (캠퍼스·식당 선택)
- 설정: 로그인/로그아웃, 알림, 테마

홈 위젯(`ScheduleWidget`)은 `presentation/widget/`에 위치한다.

---

## 직렬화

`kotlinx.serialization` 사용. `Json` 설정: `ignoreUnknownKeys = true`, `coerceInputValues = true`, `encodeDefaults = true`.
