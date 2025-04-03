package kr.hhplus.be.server.domain.coupon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "쿠폰 목록 응답")
public class CouponListResponse {

    @Schema(description = "쿠폰 목록")
    private List<CouponResponse> coupons;
}
