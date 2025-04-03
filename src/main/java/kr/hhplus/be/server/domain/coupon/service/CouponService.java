package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.dto.request.CreateCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.request.IssueCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponResponse;

public interface CouponService {
    /**
     * 쿠폰을 생성합니다 (관리자 또는 판매자 권한 필요)
     */
    CouponResponse createCoupon(CreateCouponRequest request);

    /**
     * 사용자에게 쿠폰을 발급합니다
     */
    CouponResponse issueCoupon(IssueCouponRequest request);

    /**
     * 사용자의 보유 쿠폰 목록을 조회합니다
     */
    CouponListResponse getUserCoupons(Long userId, String status);

    /**
     * 한정된 수량의 쿠폰을 발급합니다
     */
    CouponResponse limitedIssueCoupon(IssueCouponRequest request);
}