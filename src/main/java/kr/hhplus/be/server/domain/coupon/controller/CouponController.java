package kr.hhplus.be.server.domain.coupon.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.CouponAPI;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.coupon.dto.request.CreateCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.request.IssueCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponResponse;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponAPI {

    private final CouponService couponService;

    @Override
    public ResponseEntity<CustomApiResponse<CouponResponse>> createCoupon(@Valid CreateCouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<CouponResponse>> issueCoupon(@Valid IssueCouponRequest request) {
        CouponResponse response = couponService.issueCoupon(request);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<CouponListResponse>> getUserCoupons(Long userId, String status) {
        CouponListResponse response = couponService.getUserCoupons(userId, status);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<CouponResponse>> issueLimitedCoupon(IssueCouponRequest request) {
        CouponResponse response = couponService.limitedIssueCoupon(request);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}