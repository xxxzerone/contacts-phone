# [PRD] 시스템 연락처 연동 및 초성 검색 애플리케이션
## 1. 프로젝트 개요

사용자의 디바이스 내 시스템 연락처를 불러와 리스트로 시각화하고, 한국어 특화 기능인 초성 검색 및 즐겨찾기 저장 기능을 제공하는 효율적인 연락처 관리 도구를 구축합니다.

---
## 2. 주요 기능 요구사항 (Features)
### 2.1 연락처 목록 동기화

- 권한 관리: READ_CONTACTS 권한을 요청하고, 거부 시 가이드 문구를 노출합니다.
- 데이터 로드: ContentResolver와 ContactsContract API를 사용하여 시스템 연락처의 이름, 전화번호, 프로필 사진 정보를 가져옵니다.

### 2.2 초성 검색 (Consonant Search)

- 검색 로직: 일반 텍스트 검색뿐만 아니라, 한글 초성(예: 'ㄱㄷㅎ' -> '길동현') 검색을 지원합니다.
- 반응형 UI: 검색어 입력 시 즉각적으로 리스트가 필터링되어야 합니다.

### 2.3 즐겨찾기 시스템

- 로컬 저장소: 선택한 연락처를 로컬 DB(Room)에 저장하여 앱 종료 후에도 유지합니다.
- UI 피드백: 즐겨찾기 등록 시 별 모양 아이콘 등으로 상태를 표시합니다.

---
## 3. 아키텍처 설계 (Architecture)

본 프로젝트는 유지보수와 확장성을 위해 Clean Architecture를 기반으로 계층을 분리하며, MVVM 또는 MVI 패턴을 선택적으로 적용합니다.
### 3.1 계층별 역할
| 계층 (Layer)                                                                          | 	구성 요소                                                                        | 	상세 역할                                     |
|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|--------------------------------------------|
| Presentation                                                                        | 	UI (Compose/XML), ViewModel                                                  | 	UI 상태 관리 및 사용자 이벤트 처리 (MVI의 경우 Intent 처리) |
| Domain| 	Entity, UseCase, Repository Interface| 	비즈니스 로직의 핵심. 시스템/DB 의존성 없이 순수 코틀린으로 작성    |
|Data|	Repository Implementation, DataSource|	ContentResolver(시스템) 및 Room(로컬 DB) 데이터 접근|

---
## 4. 데이터 흐름 및 패턴 (MVVM/MVI)
### A. MVVM 방식

- View → ViewModel: 사용자 입력 감지.
- ViewModel → UseCase: 데이터 요청 및 StateFlow 업데이트.
- View: StateFlow를 구독하여 UI 업데이트.

### B. MVI 방식 (권장)

- Intent: LoadContacts, Search(query), ToggleFavorite(id) 등 사용자 의도를 정의.
- State: UI의 전체 상태를 하나의 Data Class로 관리 (isLoading, contacts, searchQuery).
- Effect: 토스트 메시지나 페이지 이동 등 단발성 이벤트 처리.

---
## 5. 기술 스택 (Technical Stacks)

- Language: Kotlin
- UI: Jetpack Compose
- Local DB: Room (즐겨찾기 저장용)
- Concurrency: Coroutines & Flow (비동기 처리)
- Dependency Injection: Hilt (계층 간 결합도 감소)
- Library: HangulParser (초성 추출용 커스텀 로직 또는 라이브러리)

---
## 6. 상세 구현 가이드라인
[Step 1] ContentResolver를 통한 데이터 쿼리
```kotlin
// Data 계층의 DataSource 예시
val cursor = contentResolver.query(
    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    projection, null, null, null
)
```
[Step 2] 초성 검색 로직 구현

한글의 유니코드 값을 분석하여 초성(ㄱ, ㄴ, ㄷ...)을 추출하는 유틸리티 함수를 작성하여 검색 필터링 시 적용합니다.
[Step 3] 즐겨찾기 DB (Room)

시스템 연락처의 Lookup_Key 또는 ID를 기본키로 활용하여 중복 저장을 방지하고 시스템 데이터와 매핑합니다.

---

## 7. Mock 구현
기능 구현 실행해 테스트 해볼 수 있도록 Mock 객체 구현