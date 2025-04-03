package kr.hhplus.be.server.domain.coupon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "쿠폰 응답")
public class CouponResponse {

    @Schema(description = "사용자 쿠폰 ID", example = "5001")
    private Long userCouponId;

    @Schema(description = "사용자 ID", example = "12345")
    private Long userId;

    @Schema(description = "쿠폰 유형", example = "PERCENTAGE_10")
    private String couponType;

    @Schema(description = "할인율", example = "10")
    private Integer discountRate;

    @Schema(description = "발급 시간", example = "2025-04-02T10:20:30Z")
    private LocalDateTime issuedAt;

    @Schema(description = "만료 시간", example = "2025-05-02T23:59:59Z")
    private LocalDateTime expiryDate;
}
