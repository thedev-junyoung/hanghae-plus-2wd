package kr.hhplus.be.server.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "주문 응답")
public class OrderResponse {

    @Schema(description = "주문 ID", example = "9876")
    private Long orderId;

    @Schema(description = "사용자 ID", example = "12345")
    private Long userId;

    @Schema(description = "주문 일시", example = "2025-04-02T10:25:30Z")
    private LocalDateTime orderDate;

    @Schema(description = "주문 상태", example = "PAID")
    private String status;

    @Schema(description = "주문 상품 목록")
    private List<OrderItemDTO> items;

    @Schema(description = "총 주문 금액", example = "1800000")
    private BigDecimal totalAmount;

    @Schema(description = "할인 금액", example = "180000")
    private BigDecimal discountAmount;

    @Schema(description = "최종 결제 금액", example = "1620000")
    private BigDecimal finalAmount;

    @Schema(description = "적용된 쿠폰 정보")
    private AppliedCoupon appliedCoupon;

    @Schema(description = "결제 후 잔여 잔액", example = "55000")
    private BigDecimal remainingBalance;
}
