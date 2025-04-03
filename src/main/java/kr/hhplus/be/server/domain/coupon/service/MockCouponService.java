package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.coupon.dto.request.CreateCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.request.IssueCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MockCouponService implements CouponService {

    private final AtomicLong couponIdGenerator = new AtomicLong(1L);
    private final Map<Long, List<CouponResponse>> userCouponStore = new ConcurrentHashMap<>();
    private final Set<String> couponCodes = Collections.synchronizedSet(new HashSet<>());

    // 원본 쿠폰 정보를 저장하기 위한 별도의 저장소 (Mock DB)
    private final Map<String, Object> mockCouponDB = new ConcurrentHashMap<>();

    @Override
    public CouponResponse createCoupon(CreateCouponRequest request) {
        // 쿠폰 코드 중복 확인
        if (couponCodes.contains(request.getCode())) {
            throw new BusinessException(ErrorCode.COUPON_CODE_ALREADY_EXISTS);
        }

        // 유효기간 검증
        if (request.getValidFrom().isAfter(request.getValidUntil())) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }

        // Mock 발급자 ID (실제 구현에서는 인증 정보에서 추출)
        Long issuerId = 1001L;
        Long couponId = couponIdGenerator.getAndIncrement();

        // 실제로는 DB에 저장될 쿠폰 정보 (Mock)
        Map<String, Object> couponData = new HashMap<>();
        couponData.put("id", couponId);
        couponData.put("issuerId", issuerId);
        couponData.put("code", request.getCode());
        couponData.put("type", request.getType());
        couponData.put("discountRate", request.getDiscountRate());
        couponData.put("totalQuantity", request.getTotalQuantity());
        couponData.put("remainingQuantity", request.getTotalQuantity());
        couponData.put("validFrom", request.getValidFrom());
        couponData.put("validUntil", request.getValidUntil());
        couponData.put("targetUserId", request.getTargetUserId());
        couponData.put("createdAt", LocalDateTime.now());

        // Mock DB에 저장
        mockCouponDB.put(request.getCode(), couponData);
        couponCodes.add(request.getCode());

        // 클라이언트에 반환할 응답 객체 (CouponResponse DTO 형식에 맞춤)
        CouponResponse response = CouponResponse.builder()
                .userCouponId(null) // 아직 사용자에게 발급되지 않음
                .userId(null) // 아직 사용자에게 발급되지 않음
                .couponType(request.getType())
                .discountRate(request.getDiscountRate())
                .issuedAt(LocalDateTime.now())
                .expiryDate(request.getValidUntil())
                .build();

        // TODO: 트랜잭셔널 아웃박스 패턴 - 쿠폰 생성 이벤트를 COUPON_EVENTS 테이블에 저장
        System.out.println("쿠폰 생성 이벤트 저장: 쿠폰 코드 " + request.getCode());

        return response;
    }

    @Override
    public CouponResponse issueCoupon(IssueCouponRequest request) {
        if (request.getUserId() < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 쿠폰 코드 유효성 검증
        String couponCode = request.getCouponCode();
        if (!couponCodes.contains(couponCode)) {
            throw new BusinessException(ErrorCode.COUPON_NOT_FOUND);
        }

        // Mock DB에서 쿠폰 정보 조회
        Map<String, Object> couponData = (Map<String, Object>) mockCouponDB.get(couponCode);

        // 남은 수량 확인
        int remainingQuantity = (int) couponData.get("remainingQuantity");
        if (remainingQuantity <= 0) {
            throw new BusinessException(ErrorCode.COUPON_EXHAUSTED);
        }

        // 쿠폰 수량 감소
        couponData.put("remainingQuantity", remainingQuantity - 1);

        // 유효기간 확인
        LocalDateTime validUntil = (LocalDateTime) couponData.get("validUntil");
        if (validUntil.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        // 사용자에게 쿠폰 발급
        CouponResponse coupon = CouponResponse.builder()
                .userCouponId(couponIdGenerator.getAndIncrement())
                .userId(request.getUserId())
                .couponType((String) couponData.get("type"))
                .discountRate((Integer) couponData.get("discountRate"))
                .issuedAt(LocalDateTime.now())
                .expiryDate(validUntil)
                .build();

        userCouponStore.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(coupon);

        return coupon;
    }

    @Override
    public CouponListResponse getUserCoupons(Long userId, String status) {
        if (userId < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<CouponResponse> allCoupons = userCouponStore.getOrDefault(userId, Collections.emptyList());

        // status에 따라 필터링
        List<CouponResponse> filtered;
        if ("UNUSED".equals(status)) {
            filtered = allCoupons.stream()
                    .filter(c -> c.getExpiryDate().isAfter(LocalDateTime.now()))
                    .toList();
        } else if ("EXPIRED".equals(status)) {
            filtered = allCoupons.stream()
                    .filter(c -> c.getExpiryDate().isBefore(LocalDateTime.now()))
                    .toList();
        } else {
            // ALL 또는 기타 상태
            filtered = new ArrayList<>(allCoupons);
        }

        return CouponListResponse.builder()
                .coupons(filtered)
                .build();
    }
}