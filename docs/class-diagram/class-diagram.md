# 클래스 다이어그램
```mermaid
classDiagram
%% 프레젠테이션 계층 (컨트롤러)
    class OrderController {
        -orderService: OrderService
        +createOrder(OrderRequestDto): OrderResponseDto
        +getOrderById(Long): OrderResponseDto
        +cancelOrder(Long): OrderResponseDto %%주문 취소 API 추가
    }

    class ProductController {
        -productService: ProductService
        +getAllProducts(): List~ProductResponseDto~
        +getProductById(Long): ProductResponseDto
        +getPopularProducts(): List~ProductResponseDto~ %%최근 3일간 인기 상품 조회 API
    }

    class UserController {
        -balanceService: BalanceService
        +chargeBalance(Long, ChargeRequestDto): BalanceResponseDto %%잔액 충전 API
        +getBalance(Long): BalanceResponseDto %%잔액 조회 API
    }

    class CouponController {
        -couponService: CouponService
        +issueCoupon(Long, String): CouponResponseDto
        +issueLimitedCoupon(Long, String): CouponResponseDto %%선착순 쿠폰 발급 API
        +getUserCoupons(Long): List~CouponResponseDto~ %%사용자 보유 쿠폰 목록 조회 API
    }

    class PaymentController {
        -paymentService: PaymentService
        +requestPayment(Long): PaymentResponseDto %%결제 요청 API
        +confirmPayment(String): PaymentResponseDto
    }

%% 애플리케이션 계층 (서비스)
    class OrderService {
        -orderRepository: OrderRepository
        -productService: ProductService
        -couponService: CouponService
        -eventPublisher: EventPublisher %%이벤트 발행 추가
        +createOrder(OrderRequestDto): OrderResponseDto %%주문 생성 (재고 확인 및 차감)
        +getOrderById(Long): OrderResponseDto
        +cancelOrder(Long): OrderResponseDto %%주문 취소 메서드 추가
    }

    class PaymentService {
        -paymentRepository: PaymentRepository
        -balanceService: BalanceService
        -orderRepository: OrderRepository
        -externalPaymentGateway: ExternalPaymentGateway
        -distributedLock: DistributedLock %%분산 락 추가
        +requestPayment(Long): PaymentResponseDto %%결제 요청 처리
        +confirmPayment(String): PaymentResponseDto
    }

    class ProductService {
        -productRepository: ProductRepository
        -orderItemRepository: OrderItemRepository %%인기 상품 조회를 위한 주문 아이템 저장소 추가
        -distributedLock: DistributedLock %%분산 락 추가
        +getAllProducts(): List~ProductResponseDto~
        +getProductById(Long): ProductResponseDto
        +decreaseStock(Long, int): boolean %%재고 차감 (동시성 제어)
        +getPopularProducts(): List~ProductResponseDto~ %%최근 3일간 인기 상품 조회
    }

    class BalanceService {
        -balanceRepository: BalanceRepository
        -distributedLock: DistributedLock %%분산 락 추가
        +chargeBalance(Long, BigDecimal): BalanceResponseDto %%잔액 충전
        +getBalance(Long): BalanceResponseDto
        +decreaseBalance(Long, BigDecimal): boolean %%결제 시 잔액 차감 (동시성 제어)
    }

    class CouponService {
        -couponRepository: CouponRepository
        -distributedLock: DistributedLock %%분산 락 추가
        -redisTemplate: RedisTemplate %%선착순 쿠폰을 위한 Redis 추가
        +createCoupon(CreateCouponCommand): CouponResponseDto %% 발급 주체 명확히
        +issueCoupon(Long, String): CouponResponseDto
        +getUserCoupons(Long): List~CouponResponseDto~
        +validateAndUseCoupon(Long, Long): boolean %%쿠폰 유효성 검증 및 사용 처리
        +issueLimitedCoupon(Long userId, String code): CouponResponseDto %%선착순 쿠폰 발급 (동시성 제어)
    }
    class CreateCouponCommand {
    <<Value Object>>
    -issuerId: UserId
    -type: CouponType
    -discountRate: DiscountRate
    -totalQuantity: int
    -validFrom: LocalDateTime
    -validUntil: LocalDateTime
    -targetUserId: UserId
    -code: String
    }
    
%% 이벤트 처리 추가
    class EventPublisher {
        +publish(DomainEvent): void
    }

    class OrderCreatedEvent {
        -orderId: OrderId
        -orderDetails: OrderDetails
    }

    class PaymentCompletedEvent {
        -paymentId: PaymentId
        -orderId: OrderId
    }

%% 분산 락 추가
    class DistributedLock {
        +acquire(String, Duration): boolean
        +release(String): void
        +executeWithLock(String, Callable): Object
    }

%% 아웃박스 패턴 연동 구조 추가

class OutboxEvent {
-id: UUID
-aggregateType: String
-aggregateId: String
-eventType: String
-payload: String
-status: OutboxStatus
-createdAt: LocalDateTime
}

class OutboxRepository {
<<interface>>
+save(OutboxEvent): void
+findPending(): List~OutboxEvent~
+markAsSent(UUID): void
}

class OutboxService {
-outboxRepository: OutboxRepository
+publish(DomainEvent): void %% 이벤트 → OutboxEvent 변환 후 저장
}

class EventRelayScheduler {
-outboxRepository: OutboxRepository
-externalPlatformClient: ExternalPlatformClient
+run(): void %% 주기적으로 OutboxEvent 처리
}





%% 도메인 모델 (엔티티)

    class User {
        -id: UserId
        -name: String
        -email: String
        -role: UserRole
        -businessNumber: String
        -adminCode: String
        -description: String
    }


    class Order {
        -id: OrderId
        -userId: UserId
        -items: List~OrderItem~
        -totalAmount: Money
        -discountAmount: Money
        -finalAmount: Money
        -couponId: CouponId
        -status: OrderStatus
        -orderDate: OrderDate
        +calculateTotalAmount(): Money
        +applyDiscount(Coupon): void
        +cancel(): void %%주문 취소 메서드 추가
    }

    class Payment {
        -id: PaymentId
        -orderId: OrderId
        -amount: Money
        -status: PaymentStatus
        -pgTransactionId: String
        -method: String
        -retryCount: int
        -failReason: String
        -createdAt: LocalDateTime
        -updatedAt: LocalDateTime
    }

    class Product {
        -id: ProductId
        -name: ProductName
        -price: Money
        -stock: Stock
        -version: Version %%낙관적 락을 위한 버전 필드
        +decreaseStock(Quantity): boolean %%재고 차감 (동시성 제어)
        +increaseStock(Quantity): void %%재고 증가 (주문 취소 시)
        +isAvailable(Quantity): boolean %%주문 가능 여부 확인
    }

    class Balance {
        -id: BalanceId
        -userId: UserId
        -amount: Money
        -version: Version %%낙관적 락을 위한 버전 필드
        +charge(Money): void %%잔액 충전
        +decrease(Money): boolean %%잔액 차감 (동시성 제어)
        +hasEnough(Money): boolean %%잔액 충분 여부 확인
    }

    class Coupon {
        -id: CouponId
        -issuerId: UserId %%  발급자 ID (SELLER / ADMIN)
        -userId: UserId
        -type: CouponType
        -code: String
        -discountRate: DiscountRate
        -expiryDate: ExpiryDate
        -used: boolean
        -version: Version
        +isValid(): boolean
        +markAsUsed(): void
        +applyDiscount(Money): Money
        +isIssuerValid(User): boolean %%  발급 권한 확인
    }

    class OrderItem {
        -id: OrderItemId
        -orderId: OrderId
        -productId: ProductId
        -productName: ProductName
        -quantity: Quantity
        -price: Money
        +calculateAmount(): Money
    }
class OrderEvents {
<<Entity>>
-id: UUID
-aggregateType: String = "Order"
-aggregateId: String
-eventType: String
-payload: String
-status: OutboxStatus
-createdAt: LocalDateTime
}

class CouponEvents {
<<Entity>>
-id: UUID
-aggregateType: String = "Coupon"
-aggregateId: String
-eventType: String
-payload: String
-status: OutboxStatus
-createdAt: LocalDateTime
}

class OrderEventsRepository {
<<interface>>
+save(OrderEvents): void
+findPending(): List~OrderEvents~
+markAsSent(UUID): void
}

class CouponEventsRepository {
<<interface>>
+save(CouponEvents): void
+findPending(): List~CouponEvents~
+markAsSent(UUID): void
}
%% 값 객체
    class Money {
<<Value Object>>
-amount: BigDecimal
-currency: Currency
+add(Money): Money
+subtract(Money): Money
+multiply(int): Money
+percentage(int): Money
}

class Quantity {
<<Value Object>>
-value: int
    }

class Stock {
<<Value Object>>
-quantity: Quantity
+decrease(Quantity): Stock %%재고 차감
+increase(Quantity): Stock %%재고 증가
+isAvailable(Quantity): boolean %%재고 가용성 체크
}

%% 리포지토리
class OrderRepository {
<<interface>>
+save(Order): Order
+findById(OrderId): Optional~Order~
+findByUserIdAndStatus(UserId, OrderStatus): List~Order~
}

class OrderItemRepository {
<<interface>>
+save(OrderItem): OrderItem
+findByOrderId(OrderId): List~OrderItem~
+findTopSellingProductsAfter(LocalDateTime, int): List~TopSellingProductDto~ %%인기 상품 조회 메서드 추가
}

class PaymentRepository {
<<interface>>
+save(Payment): Payment
+findById(PaymentId): Optional~Payment~
+findByOrderId(OrderId): Optional~Payment~
}

class ProductRepository {
<<interface>>
+findAll(): List~Product~
+findById(ProductId): Optional~Product~
+findByIdWithLock(ProductId): Optional~Product~ %%비관적 락을 이용한 조회
+save(Product): Product
+findTopSellingProducts(LocalDateTime, int): List~Product~ %%인기 상품 조회 메서드 추가
}

class BalanceRepository {
<<interface>>
+findByUserId(UserId): Optional~Balance~
+findByUserIdWithLock(UserId): Optional~Balance~ %%비관적 락을 이용한 조회
+save(Balance): Balance
}

class CouponRepository {
<<interface>>
+save(Coupon): Coupon
+findById(CouponId): Optional~Coupon~
+findByUserId(UserId): List~Coupon~
+findByUserIdAndCode(UserId, String): Optional~Coupon~
+findByIdWithLock(CouponId): Optional~Coupon~ %%비관적 락을 이용한 조회
}

%% 외부 시스템 연동
class ExternalPlatformClient {
+sendOrderData(OrderData): void %%주문 데이터 외부 전송 (데이터 플랫폼)
}

class ExternalPaymentGateway {
+requestPayment(Order): PaymentResponseDto
+confirmPayment(String): PaymentResponseDto
}

%% 열거형
class OrderStatus {
<<enumeration>>
CREATED
CONFIRMED
CANCELLED
DELIVERED
}

class PaymentStatus {
<<enumeration>>
INITIATED
PENDING
SUCCESS
FAILURE
CANCELLED
}

class CouponType {
<<enumeration>>
PERCENTAGE_10
PERCENTAGE_20
FIXED_AMOUNT_5000
    }

%% 관계 정의
OrderController --> OrderService
ProductController --> ProductService
UserController --> BalanceService
CouponController --> CouponService
PaymentController --> PaymentService

OrderService --> OrderRepository
OrderService --> ProductService
OrderService --> CouponService
OrderService --> EventPublisher

PaymentService --> PaymentRepository
PaymentService --> BalanceService
PaymentService --> OrderRepository
PaymentService --> ExternalPaymentGateway
PaymentService --> DistributedLock

ProductService --> ProductRepository
ProductService --> OrderItemRepository
ProductService --> DistributedLock

BalanceService --> BalanceRepository
BalanceService --> DistributedLock

CouponService --> CouponRepository
CouponService --> DistributedLock
CouponService --> RedisTemplate
CouponService --> OutboxService  


OrderRepository --> Order
OrderItemRepository --> OrderItem
PaymentRepository --> Payment
ProductRepository --> Product
BalanceRepository --> Balance
CouponRepository --> Coupon

OutboxService --> OutboxRepository
EventRelayScheduler --> OutboxRepository
EventRelayScheduler --> ExternalPlatformClient
OutboxService --> DomainEvent
OutboxService --> OrderEventsRepository
OutboxService --> CouponEventsRepository
EventRelayScheduler --> OrderEventsRepository
EventRelayScheduler --> CouponEventsRepository

Order *-- "many" OrderItem
Order *-- Money
OrderItem *-- Money
OrderItem *-- Quantity
Product *-- Stock
Product *-- Money
Stock *-- Quantity
Balance *-- Money
Coupon *-- Money

EventPublisher --> OrderCreatedEvent
EventPublisher --> PaymentCompletedEvent
`````
