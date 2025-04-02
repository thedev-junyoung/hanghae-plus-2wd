package kr.hhplus.be.server.domain.order.controller;

import kr.hhplus.be.server.api.OrderAPI;
import kr.hhplus.be.server.common.dto.Pagination;
import kr.hhplus.be.server.common.response.CustomApiResponse;
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
        BigDecimal discountAmount = BigDecimal.valueOf(500);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        OrderResponse response = OrderResponse.builder()
                .orderId(newOrderId)
                .userId(request.getUserId())
                .status("PAID")
                .items(items)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .appliedCoupon(buildMockCouponIfExists(request.getUserCouponId()))
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
                .status("PAID")
                .items(items)
                .totalAmount(BigDecimal.valueOf(3000))
                .discountAmount(BigDecimal.valueOf(1000))
                .finalAmount(BigDecimal.valueOf(2000))
                .appliedCoupon(buildMockCouponIfExists(5001L))
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<OrderListResponse>> getUserOrders(Long userId, int page, int size, String status) {
        OrderSummaryDTO summary = OrderSummaryDTO.builder()
                .orderId(1001L)
                .orderDate(LocalDateTime.now().minusDays(1))
                .status("PAID")
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

