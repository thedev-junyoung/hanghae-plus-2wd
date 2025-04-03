package kr.hhplus.be.server.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 요약 정보")
public class OrderSummaryDTO {
    @Schema(description = "주문 ID", example = "9876")
    private Long orderId;

    @Schema(description = "주문 일시", example = "2025-04-02T10:25:30Z")
    private LocalDateTime orderDate;

    @Schema(description = "주문 상태", example = "PAID")
    private String status;

    @Schema(description = "총 주문 금액", example = "1800000")
    private BigDecimal totalAmount;

    @Schema(description = "최종 결제 금액", example = "1620000")
    private BigDecimal finalAmount;

    @Schema(description = "주문 상품 개수", example = "2")
    private int itemCount;
}
