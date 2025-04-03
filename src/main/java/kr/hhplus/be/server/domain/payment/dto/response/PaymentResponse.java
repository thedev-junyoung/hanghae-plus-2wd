// 결제 응답 DTO
package kr.hhplus.be.server.domain.payment.dto.response;

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
@Schema(description = "결제 정보")
public class PaymentResponse {

    @Schema(description = "결제 ID", example = "5001")
    private Long paymentId;

    @Schema(description = "주문 ID", example = "1001")
    private Long orderId;

    @Schema(description = "사용자 ID", example = "12345")
    private Long userId;

    @Schema(description = "결제 금액", example = "50000")
    private BigDecimal amount;

    @Schema(description = "결제 상태", example = "SUCCESS", allowableValues = {"SUCCESS", "PENDING", "FAILED"})
    private String status;

    @Schema(description = "결제 수단", example = "BALANCE", allowableValues = {"BALANCE", "CARD"})
    private String method;

    @Schema(description = "외부 결제 시스템 트랜잭션 ID", example = "PG_1001_1651234567890")
    private String pgTransactionId;

    @Schema(description = "결제 생성 시간", example = "2025-04-01T14:30:00Z")
    private LocalDateTime createdAt;
}