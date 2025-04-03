package kr.hhplus.be.server.domain.order.controller;

import kr.hhplus.be.server.api.OrderAPI;
import kr.hhplus.be.server.common.dto.Pagination;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.order.dto.request.CreateOrderRequest;
import kr.hhplus.be.server.domain.order.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderAPI {

    private final AtomicLong orderIdGenerator = new AtomicLong(1000L);

    @Override
    public ResponseEntity<CustomApiResponse<OrderResponse>> createOrder(CreateOrderRequest request) {
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

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<OrderResponse>> getOrder(Long orderId) {
        List<OrderItemDTO> items = List.of(
                OrderItemDTO.builder()
                        .productId(1L)
                        .productName("Mock 상품")
                        .quantity(2)
                        .price(BigDecimal.valueOf(1000))
                        .amount(BigDecimal.valueOf(2000))
                        .build()
        );

        OrderResponse response = OrderResponse.builder()
                .orderId(orderId)
                .userId(123L)
                .status("CREATED") // 상태에 따라 다르게 표시 (CREATED, PAID, CANCELLED, DELIVERED)
                .items(items)
                .totalAmount(BigDecimal.valueOf(3000))
                .discountAmount(BigDecimal.valueOf(1000))
                .finalAmount(BigDecimal.valueOf(2000))
                .appliedCoupon(buildMockCouponIfExists(5001L))
                .orderDate(LocalDateTime.now().minusDays(1))
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<OrderListResponse>> getUserOrders(Long userId, int page, int size, String status) {
        OrderSummaryDTO summary = OrderSummaryDTO.builder()
                .orderId(1001L)
                .orderDate(LocalDateTime.now().minusDays(1))
                .status("CREATED") // 상태에 따라 다르게 표시
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

        OrderListResponse response = OrderListResponse.builder()
                .orders(List.of(summary))
                .pagination(pagination)
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(response));
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