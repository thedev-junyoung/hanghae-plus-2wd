#  스니커즈 이커머스 플랫폼 요구사항 명세서

## 1. 프로젝트 개요

본 프로젝트는 스니커즈 판매를 위한 이커머스 플랫폼의 백엔드 시스템을 구현합니다.
사용자는 사전에 충전된 잔액으로 상품을 주문하고 결제할 수 있으며, 관리자, 판매자, 구매자 간의 역할을 구분하여 서비스를 제공합니다. 핵심 목표는 테스트 가능한 설계, 객체지향 원칙 준수, 그리고 동시성과 분산 환경을 고려한 시스템 구현입니다.

## 2. 핵심 요구사항

### 2.1 사용자 관리
- 단일 User 테이블에서 ADMIN, SELLER, BUYER로 역할 구분 (싱글 테이블 상속 전략)
- 각 역할별 권한 및 기능 제한 구현
- 판매자(SELLER)는 쿠폰 발급 권한 보유
- 사용자는 역할에 따라 추가 정보 관리
  - SELLER: 사업자 등록번호, 소개글 관리
  - ADMIN: 관리자 식별 코드, 소개글 관리
  - BUYER: 기본 정보만 관리

### 2.2 잔액 관리
- 사용자 식별자를 통한 잔액 충전 기능
- 충전 금액은 0보다 큰 값만 허용
- 유효하지 않은 사용자 ID 검증
- 사용자의 잔액 조회 기능
- 동시 요청 시에도 데이터 일관성 보장
  - 낙관적 락(version 필드)을 통한 동시성 제어
  - 잔액 차감 시 충분한 금액 검증 로직 포함
- 잔액 충전 프로세스:
  - 사용자 잔액 조회 (with Lock)
  - 잔액 충전 처리
  - 잔액 업데이트 저장
  - 트랜잭션 커밋

### 2.3 상품 관리
- 상품 정보(ID, 이름, 가격, 재고) 조회 기능
- 실시간 재고 정보 정확성 보장
- 상품 판매 상태(판매중/품절) 표시
- 필터링 기능(가격순, 최신순, 카테고리별)
- 상품명 검색 기능
- 상품 재고 상태 관리:
  - AVAILABLE: 판매 가능 상태
  - RESERVED: 주문 예약 상태
  - LOW_STOCK: 임계치 이하 재고 상태
  - SOLD: 판매 완료
  - OUT_OF_STOCK: 재고 소진
  - DISCONTINUED: 판매 중단
- 상품 조회 성능 최적화:
  - 캐시 우선 조회 전략(Cache-Aside Pattern) 적용
  - 캐시 미스 시 DB 조회 후 캐시 저장
  - 조회 빈도가 높은 데이터에 TTL 설정

### 2.4 쿠폰 관리
- 쿠폰 생성 및 관리
  - 쿠폰 코드 유니크성 보장
  - 할인 타입(PERCENTAGE, FIXED) 지원
  - 발급 가능 수량 및 유효기간 설정
- 선착순 쿠폰 발급 기능
  - Redis 기반 동시성 제어 적용
  - 쿠폰 수량 소진 시 자동 종료
- 쿠폰 상태 관리
  - CREATED: 쿠폰 생성 완료
  - ACTIVE: 발급 가능 상태
  - EXHAUSTED: 수량 소진
  - EXPIRED: 유효기간 만료
  - SUSPENDED: 일시 중지
  - CANCELLED: 운영자 취소
- 사용자 쿠폰 상태 관리
  - ISSUED: 사용자에게 발급
  - USED: 주문에 적용
  - EXPIRED: 유효기간 만료
  - REFUNDED: 주문 취소/환불로 재사용 가능
- 쿠폰 발급 권한 검증 로직
  - 발급자(SELLER/ADMIN) 권한 확인
  - 쿠폰 발급 이벤트 기록
- 쿠폰 발급 프로세스:
  - 발급자 권한 검증
  - 쿠폰 코드 중복 확인
  - 쿠폰 정보 저장
  - 이벤트 저장 (COUPON_EVENTS 테이블)
  - 외부 트리거 시스템으로 비동기 전송

### 2.5 주문 및 결제
- 주문 프로세스
  - 주문 항목 검증
  - 재고 확인 (부족 시 실패 처리)
  - 쿠폰 검증 및 적용 (무효 시 쿠폰 없이 진행)
  - 주문 금액 계산
  - 결제 진행
- 주문 상태 관리
  - CREATED: 주문 생성
  - CONFIRMED: 결제 성공
  - PREPARING: 주문 준비 중
  - SHIPPED: 배송 시작
  - DELIVERED: 배송 완료
  - COMPLETED: 구매 확정
  - CANCELLED: 주문 취소
- 결제 프로세스
  - 주문 상태 확인 (CREATED만 결제 가능)
  - 결제 정보 초기 저장 (INITIATED)
  - 잔액 차감 시도
  - 외부 결제 게이트웨이 승인 요청
  - 승인 성공 시 결제 상태 SUCCESS로 업데이트
  - 주문 상태 CONFIRMED로 업데이트
  - 결제 완료 이벤트 저장 (ORDER_EVENTS 테이블)
  - 비동기 이벤트 전송
- 결제 상태 관리
  - INITIATED: 결제 요청 시작
  - VALIDATING: 주문 & 사용자 유효성 검증
  - LOCK_ACQUIRED: 잔액 락 획득
  - DEBITED: 잔액 차감 성공
  - GATEWAY_REQUESTED: 외부 PG 결제 요청
  - COMPLETED: 결제 성공
  - FAILED: 결제 실패
- 환불 프로세스
  - REFUND_REQUESTED: 환불 요청
  - PROCESSING: 환불 처리 중
  - REFUNDED: 환불 완료
  - FAILED: 환불 실패
  - MANUAL: 수동 환불 처리
- 주문 생성 프로세스:
  - 상품 유효성 및 재고 확인
  - 재고 충분 시 재고 차감
  - 주문 금액 계산
  - 주문 정보 저장 (상태: CREATED)
  - 주문 생성 이벤트 저장 (ORDER_EVENTS 테이블)
  - 트랜잭션 커밋

### 2.6 통계 및 추천
- 최근 3일간 가장 많이 판매된 상위 5개 상품 조회
- 판매량 기준 인기 상품 추천 기능
- 다양한 기준의 통계 조회(판매액, 카테고리별, 기간별)
- 인기 상품 통계 처리 과정
  - IDLE: 대기 상태
  - AGGREGATING: 판매 데이터 집계
  - CACHE_UPDATED: 인기 상품 캐시 저장
  - EXPIRED: TTL 만료 (재집계 필요)
- 인기 상품 조회 프로세스:
  - 캐시 확인 (캐시 있으면 반환)
  - 캐시 없을 경우 최근 3일간 판매 통계 집계
  - 상위 5개 상품 정보 조회
  - 결과 캐싱 (TTL 설정)

### 2.7 외부 시스템 연동
- 결제 성공 시 데이터 플랫폼으로 주문 정보 전송
- 트랜잭셔널 아웃박스 패턴 적용
  - ORDER_EVENTS 테이블에 이벤트 저장
  - COUPON_EVENTS 테이블에 이벤트 저장
  - 비동기 처리로 외부 시스템에 전송
- 이벤트 상태 관리
  - PENDING: 이벤트 저장됨
  - PROCESSING: 처리 중
  - SENT: 외부 시스템 전송 완료
  - FAILED: 전송 실패
  - ALERTED: 반복 실패 후 알림 전송
- 이벤트 처리 실패 시 재시도 메커니즘 구현

## 3. 비기능적 요구사항

### 3.1 동시성 제어
- 재고 관리
  - 낙관적 락(version 필드) 적용
  - 동시 주문 시 재고 정확성 보장
- 잔액 차감
  - 낙관적 락 적용
  - 동시 결제 시 데이터 일관성 보장
- 쿠폰 발급
  - Redis 기반 분산 락 적용
  - 선착순 발급 시 정확한 수량 관리
- 분산 환경 고려
  - 분산 락 메커니즘 구현
  - 트랜잭션 격리 수준 설정

### 3.2 성능 최적화
- 자주 조회되는 데이터에 대한 캐싱 전략
  - 상품 정보 캐싱
  - 인기 상품 목록 캐싱 (TTL 기반)
- 통계 데이터 집계를 위한 최적화된 쿼리 설계
  - 인덱스 설계
  - 집계 쿼리 최적화
- API 응답 시간 최소화
  - N+1 문제 해결
  - 페이징 처리

### 3.3 신뢰성
- 외부 시스템 연동 실패에 대한 복원력 확보
  - 아웃박스 패턴을 통한 이벤트 보존
  - 실패 시 재시도 메커니즘 적용
- 트랜잭션 일관성 보장
  - ACID 속성 준수
  - 분산 트랜잭션 고려
- 결제 및 주문 프로세스의 원자성 보장
  - 결제-주문-이벤트 발행의 트랜잭션 처리
  - 롤백 메커니즘 구현

## 4. API 명세

### 4.1 사용자 및 잔액 API
- `POST /api/v1/balances/charge` - 잔액 충전
- `GET /api/v1/balances/{userId}` - 잔액 조회

### 4.2 상품 API
- `GET /api/v1/products` - 상품 목록 조회 (필터링 옵션 지원)
- `GET /api/v1/products/{productId}` - 상품 상세 조회
- `GET /api/v1/products/popular` - 인기 상품 조회

### 4.3 쿠폰 API
- `POST /api/v1/coupons` - 쿠폰 생성 (판매자/관리자용)
- `POST /api/v1/coupons/issue` - 쿠폰 발급
- `POST /api/v1/coupons/issue/limited` - 선착순 쿠폰 발급
- `GET /api/v1/coupons/user/{userId}` - 사용자 보유 쿠폰 조회

### 4.4 주문 및 결제 API
- `POST /api/v1/orders` - 주문 생성
- `GET /api/v1/orders/{orderId}` - 주문 조회
- `PUT /api/v1/orders/{orderId}/cancel` - 주문 취소
- `POST /api/v1/payments/process` - 결제 처리 요청
- `POST /api/v1/payments/confirm` - 결제 확인
- `POST /api/v1/refunds` - 환불 요청

## 5. 테스트 요구사항

- 각 기능 및 제약사항에 대한 단위 테스트 구현
- 동시성 이슈 테스트
  - 재고 차감 동시성 테스트
  - 잔액 차감 동시성 테스트
  - 쿠폰 발급 동시성 테스트
- 경계값 및 예외 상황 테스트
  - 재고 부족 시나리오
  - 잔액 부족 시나리오
  - 쿠폰 무효 시나리오
- E2E 테스트를 통한 전체 흐름 검증
  - 주문-결제-이벤트 발행 흐름
  - 쿠폰 발급-사용-환불 흐름

## 6. 프로세스 흐름 상세

### 6.1 결제 요청 프로세스
1. 결제 요청 접수 (주문 ID)
2. 주문 상태 확인 (CREATED 상태만 결제 가능)
3. 결제 정보 초기 저장 (INITIATED)
4. 잔액 차감 요청
   - 잔액 부족 시 결제 실패 처리
5. 외부 결제 게이트웨이 승인 요청
   - 승인 실패 시 잔액 복구 및 결제 실패 처리
6. 결제 상태 SUCCESS로 업데이트
7. 주문 상태 CONFIRMED로 업데이트
8. PaymentCompletedEvent 생성 및 저장 (PENDING)
9. 결제 완료 응답 반환
10. 비동기 이벤트 처리
    - PENDING 이벤트 조회 및 외부 전송
    - 실패 시 재시도 설정

### 6.2 상품 조회 프로세스
1. 상품 목록 또는 상세 조회 요청
2. 캐시 확인
   - 캐시 있음: 캐시된 데이터 반환
   - 캐시 없음: DB에서 조회 후 캐시 저장
3. 상세 조회 시 상품 없으면 404 에러 반환
4. 조회 결과 반환

### 6.3 인기 상품 조회 프로세스
1. 인기 상품 조회 요청
2. 캐시 확인
   - 캐시 있음: 캐시된 인기 상품 반환
   - 캐시 없음: 통계 집계 후 캐싱
3. 최근 3일간 판매 통계 조회 (캐시 미스 시)
4. 상위 5개 상품 상세 정보 조회
5. 인기 상품 캐시 저장 (TTL 설정)
6. 인기 상품 목록 반환

### 6.4 잔액 충전 및 조회 프로세스
1. 잔액 충전 요청 (userId, amount)
2. 트랜잭션 시작
3. 사용자 잔액 조회 (with Lock)
     - 사용자 없음: 404 에러 반환
4. 잔액 충전 처리
5. 잔액 업데이트 저장
6. 트랜잭션 커밋
7. 충전된 잔액 정보 반환

### 6.5 주문 생성 프로세스
1. 주문 생성 요청 (상품 목록, 쿠폰 ID)
2. 트랜잭션 시작
3. 상품 유효성 및 재고 확인
     - 재고 부족/유효하지 않은 상품: 400 에러 반환
4. 재고 차감
5. 주문 금액 계산
6. 주문 정보 저장 (상태: CREATED)
7. OrderCreatedEvent 발행 및 저장 (PENDING)
8. 트랜잭션 커밋
9. 주문 ID 반환
10. 비동기 이벤트 처리
      - PENDING 이벤트 조회 및 외부 전송
      - 실패 시 재시도 설정

### 6.6 쿠폰 발급 프로세스
1. 쿠폰 발급 요청 (code, type, discountRate)
2. 트랜잭션 시작
3. 발급자 권한 검증
   - 권한 없음: 403 에러 반환
4. 쿠폰 코드 중복 확인
   - 중복 코드: 409 에러 반환
5. 쿠폰 정보 저장
6. CouponIssuedEvent 생성 및 저장 (PENDING)
7. 트랜잭션 커밋
8. 쿠폰 발급 완료 응답
9. 비동기 이벤트 처리
   - PENDING 이벤트 조회 및 외부 전송
   - 실패 시 재시도 설정
