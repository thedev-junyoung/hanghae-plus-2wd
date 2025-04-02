package kr.hhplus.be.server.domain.coupon.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.CouponAPI;
import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.response.CustomApiResponse;
import kr.hhplus.be.server.domain.coupon.dto.request.IssueCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class CouponController implements CouponAPI {

    // Mock 저장소 (userId 기준 쿠폰 목록 저장)
    private final Map<Long, List<CouponResponse>> userCouponStore = new ConcurrentHashMap<>();
    private static long couponIdSequence = 1L;

    @Override
    public ResponseEntity<CustomApiResponse<CouponResponse>> issueCoupon(@Valid IssueCouponRequest request) {
        if (request.getUserId() < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 임의 쿠폰 타입 및 할인율 지정
        String couponType = "PERCENTAGE_10";
        int discountRate = 10;

        CouponResponse coupon = CouponResponse.builder()
                .userCouponId(couponIdSequence++)
                .userId(request.getUserId())
                .couponType(couponType)
                .discountRate(discountRate)
                .issuedAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build();

        userCouponStore.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(coupon);

        return ResponseEntity.ok(CustomApiResponse.success(coupon));
    }

    @Override
    public ResponseEntity<CustomApiResponse<CouponListResponse>> getUserCoupons(Long userId, String status) {
        if (userId < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<CouponResponse> allCoupons = userCouponStore.getOrDefault(userId, Collections.emptyList());

        // status에 따라 필터링 (지금은 MOCK이므로 상태 분기는 생략 가능. 필요 시 추가)
        List<CouponResponse> filtered = new ArrayList<>(allCoupons);

        CouponListResponse response = CouponListResponse.builder()
                .coupons(filtered)
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}
