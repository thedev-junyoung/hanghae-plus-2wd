package kr.hhplus.be.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.common.exception.ApiErrorResponse;
import kr.hhplus.be.server.common.response.CustomApiResponse;
import kr.hhplus.be.server.domain.coupon.dto.request.IssueCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Coupon", description = "쿠폰 관리 API")
@RequestMapping("/api/v1/coupons")
public interface CouponAPI {

    @Operation(summary = "쿠폰 발급", description = "선착순으로 할인 쿠폰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 쿠폰 없음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "동시성 충돌",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/issue")
    ResponseEntity<CustomApiResponse<CouponResponse>> issueCoupon(
            @Parameter(description = "쿠폰 발급 요청", required = true)
            @Valid @RequestBody IssueCouponRequest request
    );

    @Operation(summary = "보유 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = CouponListResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/user/{userId}")
    ResponseEntity<CustomApiResponse<CouponListResponse>> getUserCoupons(
            @Parameter(description = "사용자 ID", example = "12345", required = true)
            @PathVariable Long userId,
            @Parameter(description = "쿠폰 상태 필터 (ALL, UNUSED, USED, EXPIRED)", example = "ALL")
            @RequestParam(defaultValue = "ALL") String status
    );

    // Swagger 문서화를 위한 응답 스키마 내부 클래스


}