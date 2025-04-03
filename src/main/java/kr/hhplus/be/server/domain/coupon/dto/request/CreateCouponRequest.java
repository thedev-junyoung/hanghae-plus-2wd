package kr.hhplus.be.server.domain.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "쿠폰 생성 요청")
public class CreateCouponRequest {

    @NotBlank(message = "쿠폰 코드는 필수입니다.")
    @Schema(description = "쿠폰 코드", example = "SUMMER2025")
    private String code;

    @NotBlank(message = "쿠폰 타입은 필수입니다.")
    @Schema(description = "쿠폰 타입", example = "PERCENTAGE", allowableValues = {"PERCENTAGE", "FIXED"})
    private String type;

    @NotNull(message = "할인율/할인액은 필수입니다.")
    @Min(value = 1, message = "할인율/할인액은 최소 1 이상이어야 합니다.")
    @Schema(description = "할인율 (% 단위) 또는 할인액 (원 단위)", example = "10")
    private Integer discountRate;

    @NotNull(message = "총 발행 수량은 필수입니다.")
    @Min(value = 1, message = "총 발행 수량은 최소 1 이상이어야 합니다.")
    @Schema(description = "총 발행 수량", example = "1000")
    private Integer totalQuantity;

    @NotNull(message = "유효 시작일은 필수입니다.")
    @Schema(description = "유효 시작일", example = "2025-05-01T00:00:00")
    private LocalDateTime validFrom;

    @NotNull(message = "유효 종료일은 필수입니다.")
    @Future(message = "유효 종료일은 미래 날짜여야 합니다.")
    @Schema(description = "유효 종료일", example = "2025-05-31T23:59:59")
    private LocalDateTime validUntil;

    @Schema(description = "특정 타겟 사용자 ID (특정 사용자에게만 발급하는 경우)")
    private Long targetUserId;
}