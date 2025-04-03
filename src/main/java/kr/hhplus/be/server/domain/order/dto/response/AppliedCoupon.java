package kr.hhplus.be.server.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "적용된 쿠폰 정보")
public class AppliedCoupon {

    @Schema(description = "쿠폰 ID", example = "42")
    private Long couponId;

    @Schema(description = "쿠폰 유형", example = "PERCENTAGE_10") // !! 고민 !!
    private String couponType;

    @Schema(description = "할인율", example = "10")
    private Integer discountRate;

    @Schema(description = "만료일", example = "2025-05-01T23:59:59Z")
    private LocalDateTime expiryDate;
}
