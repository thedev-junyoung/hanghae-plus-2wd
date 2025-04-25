// 결제 처리 요청 DTO
package kr.hhplus.be.server.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "결제 처리 요청")
public class ProcessPaymentRequest {

    @NotNull(message = "주문 ID는 필수입니다.")
    @Positive(message = "주문 ID는 양수여야 합니다.")
    @Schema(description = "주문 ID", example = "1001")
    private Long orderId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    @Positive(message = "사용자 ID는 양수여야 합니다.")
    @Schema(description = "사용자 ID", example = "12345")
    private Long userId;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 양수여야 합니다.")
    @Schema(description = "결제 금액", example = "50000")
    private BigDecimal amount;
}