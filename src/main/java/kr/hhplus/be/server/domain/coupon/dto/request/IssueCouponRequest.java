package kr.hhplus.be.server.domain.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "쿠폰 발급 요청")
public class IssueCouponRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    @Schema(description = "사용자 ID", example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "쿠폰 ID는 필수입니다.")
    @Schema(description = "쿠폰 ID", example = "5001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String couponCode;

}
