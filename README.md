# 신용카드 전월 실적 및 혜택 자동 계산기 (Credit Card Benefit Calculator)

신용카드 상품 약관에 규정된 복잡한 **전월 실적 산정 제외 조건(세금, 공과금, 무이자 할부 등)** 및 ** 
카드사별 우대 혜택(할인/적립) 계산 로직**을 검증하고 처리하는 백엔드 API 계산기입니다.


---

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 4.1.1, Spring Data JPA
- **Database**: H2 (In-memory Database)
- **Build Tool**: Gradle
- **Testing**: JUnit 5, AssertJ

---

## 핵심 비즈니스 로직 및 약관 적용
1. **전월 실적 제외 검증 엔진**
    - **무이자 할부 (`isInterestFree = true`)**: 전 카드사 공통으로 실적 합산 및 혜택 대상 전면 배제
    - **세금 및 공과금 (`category = 'TAX'`)**: 전월 실적 인정 대상에서 제외 처리

2. **카드사별 우대 혜택 계산**
    - **우리카드 (카드의정석 EVERY 1)**: 실적 조건 없이 결제건의 0.8% 기본 할인/적립
    - **하나카드 (CLUB SK)**: 실적 인정 결제건에 한해 1.0% 할인/적립 적용

---

## 🚀 API Endpoint 명세

### 1. 실적 및 혜택 수동/자동 산출
- **HTTP Method**: `POST`
- **URL**: `/api/calculator/calculate/{userNum}`
- **Description**: 해당 사용자의 전체 거래 내역을 조회하여 실적 인정 여부(`isPerformanceTarget`) 및 혜택 금액(`appliedBenefitAmount`)을 계산 후 업데이트 및 반환합니다.

---

## 🗄 데이터베이스 구조 (ERD)
- **Card**: 카드사 및 카드명 정보
- **UserCard**: 사용자별 발급된 카드 정보
- **Transaction**: 결제 거래 내역 (결제금액, 무이자여부, 카테고리, 실적제외여부, 혜택금액)