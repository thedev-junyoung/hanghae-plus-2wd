package kr.hhplus.be.server.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private AppliedCouponDto appliedCoupon;
    private BigDecimal remainingBalance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedCouponDto {
        private Long userCouponId;
        private String couponType;
        private Integer discountRate;
    }
}