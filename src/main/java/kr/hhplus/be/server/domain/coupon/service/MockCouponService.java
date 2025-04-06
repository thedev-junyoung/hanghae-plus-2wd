package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.coupon.dto.request.CreateCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.request.IssueCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.domain.coupon.dto.response.CouponResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MockCouponService implements CouponService {

    private final AtomicLong couponIdGenerator = new AtomicLong(1L);
    private final Map<Long, List<CouponResponse>> userCouponStore = new ConcurrentHashMap<>();
    private final Set<String> couponCodes = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Map<String, Object>> mockCouponDB = new ConcurrentHashMap<>();

    @Override
    public CouponResponse createCoupon(CreateCouponRequest request) {
        validateCouponCode(request.getCode());
        validateDateRange(request.getValidFrom(), request.getValidUntil());

        // Mock 발급자 ID (실제 구현에서는 인증 정보에서 추출)
        Long issuerId = 1001L;
        Long couponId = couponIdGenerator.getAndIncrement();

        // 쿠폰 데이터 생성 및 저장
        Map<String, Object> couponData = createCouponData(
                couponId, issuerId, request.getCode(), request.getType(),
                request.getDiscountRate(), request.getTotalQuantity(),
                request.getValidFrom(), request.getValidUntil(),
                request.getTargetUserId()
        );

        // Mock DB에 저장
        mockCouponDB.put(request.getCode(), couponData);
        couponCodes.add(request.getCode());

        // 응답 객체 생성
        CouponResponse response = CouponResponse.builder()
                .userCouponId(null) // 아직 사용자에게 발급되지 않음
                .userId(null) // 아직 사용자에게 발급되지 않음
                .couponType(request.getType())
                .discountRate(request.getDiscountRate())
                .issuedAt(LocalDateTime.now())
                .expiryDate(request.getValidUntil())
                .build();

        // 아웃박스 패턴 이벤트 로깅
        logCouponEvent("CREATE", request.getCode());

        return response;
    }

    @Override
    public CouponResponse issueCoupon(IssueCouponRequest request) {
        validateUserId(request.getUserId());
        validateCouponExists(request.getCouponCode());

        // 쿠폰 정보 조회 및 검증
        Map<String, Object> couponData = mockCouponDB.get(request.getCouponCode());
        validateCouponAvailability(couponData);

        // 쿠폰 수량 감소
        decreaseCouponQuantity(couponData);

        // 사용자에게 쿠폰 발급
        CouponResponse coupon = createUserCoupon(
                request.getUserId(), couponData
        );

        // 사용자의 쿠폰 목록에 추가
        userCouponStore.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(coupon);

        // 이벤트 로깅
        logCouponEvent("ISSUE", request.getCouponCode());

        return coupon;
    }

    @Override
    public CouponListResponse getUserCoupons(Long userId, String status) {
        validateUserExistsForQuery(userId);  // ✅ 여기서 존재 여부 확인

        List<CouponResponse> allCoupons = userCouponStore.getOrDefault(userId, Collections.emptyList());

        Predicate<CouponResponse> predicate = switch (status) {
            case "UNUSED" -> c -> c.getExpiryDate().isAfter(LocalDateTime.now());
            case "EXPIRED" -> c -> c.getExpiryDate().isBefore(LocalDateTime.now());
            default -> c -> true;
        };

        List<CouponResponse> filtered = allCoupons.stream()
                .filter(predicate)
                .collect(Collectors.toList());

        return CouponListResponse.builder()
                .coupons(filtered)
                .build();
    }

    @Override
    public CouponResponse limitedIssueCoupon(IssueCouponRequest request) {
        validateUserId(request.getUserId());
        validateCouponExists(request.getCouponCode());

        // 쿠폰 정보 조회 및 검증
        Map<String, Object> couponData = mockCouponDB.get(request.getCouponCode());
        validateCouponAvailability(couponData);

        // 쿠폰 수량 감소
        decreaseCouponQuantity(couponData);

        // 사용자에게 쿠폰 발급
        CouponResponse coupon = createUserCoupon(
                request.getUserId(), couponData
        );

        // 사용자의 쿠폰 목록에 추가
        userCouponStore.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(coupon);

        // 이벤트 로깅
        logCouponEvent("LIMITED_ISSUE", request.getCouponCode());

        return coupon;

    }

    // ====== 헬퍼 메서드 ======

    private Map<String, Object> createCouponData(
            Long couponId, Long issuerId, String code, String type,
            Integer discountRate, Integer totalQuantity,
            LocalDateTime validFrom, LocalDateTime validUntil,
            Long targetUserId
    ) {
        Map<String, Object> couponData = new HashMap<>();
        couponData.put("id", couponId);
        couponData.put("issuerId", issuerId);
        couponData.put("code", code);
        couponData.put("type", type);
        couponData.put("discountRate", discountRate);
        couponData.put("totalQuantity", totalQuantity);
        couponData.put("remainingQuantity", totalQuantity);
        couponData.put("validFrom", validFrom);
        couponData.put("validUntil", validUntil);
        couponData.put("targetUserId", targetUserId);
        couponData.put("createdAt", LocalDateTime.now());

        return couponData;
    }

    private CouponResponse createUserCoupon(Long userId, Map<String, Object> couponData) {
        return CouponResponse.builder()
                .userCouponId(couponIdGenerator.getAndIncrement())
                .userId(userId)
                .couponType((String) couponData.get("type"))
                .discountRate((Integer) couponData.get("discountRate"))
                .issuedAt(LocalDateTime.now())
                .expiryDate((LocalDateTime) couponData.get("validUntil"))
                .build();
    }

    private void decreaseCouponQuantity(Map<String, Object> couponData) {
        int remainingQuantity = (int) couponData.get("remainingQuantity");
        couponData.put("remainingQuantity", remainingQuantity - 1);
    }

    private void logCouponEvent(String eventType, String couponCode) {
        System.out.println("쿠폰 " + eventType + " 이벤트 저장: 쿠폰 코드 " + couponCode);
    }

    // ====== 검증 메서드 ======

    private void validateCouponCode(String code) {
        if (couponCodes.contains(code)) {
            throw new BusinessException(ErrorCode.COUPON_CODE_ALREADY_EXISTS);
        }
    }

    private void validateDateRange(LocalDateTime from, LocalDateTime until) {
        if (from.isAfter(until)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }


    private void validateCouponExists(String couponCode) {
        if (!couponCodes.contains(couponCode)) {
            throw new BusinessException(ErrorCode.COUPON_NOT_FOUND);
        }
    }

    private void validateCouponAvailability(Map<String, Object> couponData) {
        // 남은 수량 확인
        int remainingQuantity = (int) couponData.get("remainingQuantity");
        if (remainingQuantity <= 0) {
            throw new BusinessException(ErrorCode.COUPON_EXHAUSTED);
        }

        // 유효기간 확인
        LocalDateTime validUntil = (LocalDateTime) couponData.get("validUntil");
        if (validUntil.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
    }
    private void validateUserExistsForQuery(Long userId) {
        if (userId == null || userId < 1 || !userCouponStore.containsKey(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }
}