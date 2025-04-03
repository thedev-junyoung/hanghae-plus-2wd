package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.common.dto.Pagination;
import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.order.dto.request.CreateOrderRequest;
import kr.hhplus.be.server.domain.order.dto.response.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MockOrderService implements OrderService {

    private final AtomicLong orderIdGenerator = new AtomicLong(1000L);
    private final Map<Long, OrderResponse> orderStore = new ConcurrentHashMap<>();

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) { // !! 고민 !!
        Long newOrderId = orderIdGenerator.getAndIncrement();

        List<OrderItemDTO> items = mapToItemDtos(request.getItems());
        BigDecimal totalAmount = calculateTotalAmount(items);

        // 쿠폰 할인 적용 (있는 경우)
        AppliedCoupon appliedCoupon = buildMockCouponIfExists(request.getUserCouponId());
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (appliedCoupon != null) {
            // 쿠폰 타입에 따라 할인액 계산
            if ("PERCENTAGE_10".equals(appliedCoupon.getCouponType())) {
                discountAmount = totalAmount.multiply(BigDecimal.valueOf(0.1));
            } else if ("FIXED_AMOUNT_5000".equals(appliedCoupon.getCouponType())) {
                discountAmount = BigDecimal.valueOf(5000);
            }
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        OrderResponse response = OrderResponse.builder()
                .orderId(newOrderId)
                .userId(request.getUserId())
                .status("CREATED") // 주문만 생성되고 결제는 아직 안 됨
                .items(items)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .appliedCoupon(appliedCoupon)
                .orderDate(LocalDateTime.now())
                .build();

        // 주문 저장
        orderStore.put(newOrderId, response);

        // TODO: 트랜잭셔널 아웃박스 패턴 - 주문 생성 이벤트를 ORDER_EVENTS 테이블에 저장
        System.out.println("주문 생성 이벤트 저장: 주문 ID " + newOrderId);

        return response;
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        OrderResponse order = orderStore.get(orderId);

        if (order == null) {
            // 실제 구현에서는 orderStore에서 없을 경우 orderRepository에서 조회할 것
            // Mock 구현을 위한 기본 응답 생성
            List<OrderItemDTO> items = List.of(
                    OrderItemDTO.builder()
                            .productId(1L)
                            .productName("Mock 상품")
                            .quantity(2)
                            .price(BigDecimal.valueOf(1000))
                            .amount(BigDecimal.valueOf(2000))
                            .build()
            );

            order = OrderResponse.builder()
                    .orderId(orderId)
                    .userId(123L)
                    .status("CREATED")
                    .items(items)
                    .totalAmount(BigDecimal.valueOf(3000))
                    .discountAmount(BigDecimal.valueOf(1000))
                    .finalAmount(BigDecimal.valueOf(2000))
                    .appliedCoupon(buildMockCouponIfExists(5001L))
                    .orderDate(LocalDateTime.now().minusDays(1))
                    .build();
        }

        return order;
    }

    @Override
    public OrderListResponse getUserOrders(Long userId, int page, int size, String status) {
        OrderSummaryDTO summary = OrderSummaryDTO.builder()
                .orderId(1001L)
                .orderDate(LocalDateTime.now().minusDays(1))
                .status("CREATED")
                .totalAmount(BigDecimal.valueOf(5000))
                .finalAmount(BigDecimal.valueOf(4500))
                .itemCount(2)
                .build();

        Pagination pagination = Pagination.builder()
                .page(page)
                .size(size)
                .totalElements(1)
                .totalPages(1)
                .build();

        return OrderListResponse.builder()
                .orders(List.of(summary))
                .pagination(pagination)
                .build();
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        // 주문 조회
        OrderResponse order = orderStore.get(orderId);

        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 사용자 일치 확인
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 주문 상태 확인 - CREATED 상태에서만 취소 가능
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // 주문 상태 변경
        OrderResponse cancelledOrder = OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .status("CANCELLED")
                .items(order.getItems())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .appliedCoupon(order.getAppliedCoupon())
                .orderDate(order.getOrderDate())
                .build();

        // 주문 저장소 업데이트
        orderStore.put(orderId, cancelledOrder);

        // TODO: 트랜잭셔널 아웃박스 패턴 - 주문 취소 이벤트를 ORDER_EVENTS 테이블에 저장
        System.out.println("주문 취소 이벤트 저장: 주문 ID " + orderId);

        return cancelledOrder;
    }

    // ======== 헬퍼 메서드 모음 ========

    private List<OrderItemDTO> mapToItemDtos(List<CreateOrderRequest.OrderItemRequest> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal price = BigDecimal.valueOf(1000);
                    return OrderItemDTO.builder()
                            .productId(item.getProductId())
                            .productName("테스트상품-" + item.getProductId())
                            .quantity(item.getQuantity())
                            .price(price)
                            .amount(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                            .build();
                })
                .toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItemDTO> items) {
        return items.stream()
                .map(OrderItemDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private AppliedCoupon buildMockCouponIfExists(Long couponId) {
        if (couponId == null) return null;
        return AppliedCoupon.builder()
                .couponId(couponId)
                .couponType("PERCENTAGE_10")
                .discountRate(10)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build();
    }
}