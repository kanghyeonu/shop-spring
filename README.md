# 쇼핑몰 프로젝트 (Shop-Spring)

## 프로젝트 소개

Spring Boot 3 기반의 실무형 쇼핑몰 프로젝트입니다.  
회원가입부터 장바구니, 주문, 결제 흐름까지의 전 과정을 **비즈니스 로직 중심으로 직접 설계하고 구현**하고자 했습니다.

---

## 기술 스택

- Java 17
- Spring Boot 3.4.5
- Gradle
- Spring Security + JWT
- MySQL (Azure)
- Redis (이메일 인증)
- JPA / Hibernate
- JUnit5 + Mockito + JaCoCo
- Notion (API 명세 정리) -> Swagger로 포팅 중

---

## 프로젝트 아키텍처

<img width="893" height="529" alt="Image" src="https://github.com/user-attachments/assets/5617eb88-86a0-4ded-9a98-4532231397dc" />

## 주요 기능

### ✅ 회원 기능
- 회원가입 (구글 이메일 인증 + Redis 인증번호)
- 로그인 (JWT 발급)
- 비밀번호 찾기 및 재설정
- 마이페이지 (내 정보, 주문 조회, 상품 관리)

### ✅ 상품 기능
- 상품 등록 / 조회 / 수정 / 삭제
- 카테고리 분류 (계층 구조 지원)

### ✅ 장바구니 & 주문
- 장바구니 담기 / 수정 / 삭제
- 단일 상품 구매, 장바구니 전체 주문
- 결제 시뮬레이션 (PG사 Mock 구현)
- 주문 상태 관리 (결제 → 배송 준비 등)

---

## 🧪 테스트 및 품질

- 유닛 테스트: 서비스 레이어 중심
- 테스트 커버리지: **80~100% (JaCoCo)**
- 커스텀 예외 + 글로벌 핸들링 적용
- 인증/인가 분리 (SecurityFilterChain 기반)

---

## 📁 프로젝트 구조
<pre> shop.shop_spring
	├── Member 
	│   ├── controller 
	│   ├── service 
	│   └── domain 
	├── Product 
	├── Order 
	├── Payment 
	├── Redis 
	├── Security 
	└── Exception </pre>


## API 문서 

- *Notion 기반으로 기능별 API 정리* 
  (https://veiled-breath-897.notion.site/1-1dc00da4588c80eaa9cfc83ab7f088cb?source=copy_link)
-> Swagger로 포팅 중
---

##  실행 방법

```bash
# 로컬에서 실행 시
./gradlew build
java -jar build/libs/shop-spring-0.0.1-SNAPSHOT.jar
