package kr.hhplus.be.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON-002", "지원하지 않는 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-003", "엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-004", "서버 내부 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON-005", "잘못된 타입의 값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON-006", "접근 권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-007", "인증이 필요합니다."),
    CONCURRENT_REQUEST(HttpStatus.CONFLICT, "COMMON-008", "동시성 요청 충돌이 발생했습니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자를 찾을 수 없습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "USER-002", "유효하지 않은 사용자 ID입니다."),

    // 상품 관련 에러
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT-001", "상품을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.UNPROCESSABLE_ENTITY, "PRODUCT-002", "재고가 부족합니다."),

    // 주문 관련 에러
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER-001", "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATE(HttpStatus.BAD_REQUEST, "ORDER-002", "유효하지 않은 주문 상태입니다."),
    INVALID_ORDER_ITEM(HttpStatus.BAD_REQUEST, "ORDER-003", "유효하지 않은 주문 상품입니다."),

    // 잔액 관련 에러
    INSUFFICIENT_BALANCE(HttpStatus.UNPROCESSABLE_ENTITY, "BALANCE-001", "잔액이 부족합니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "BALANCE-002", "유효하지 않은 금액입니다."),
    BALANCE_UPDATE_FAILED(HttpStatus.CONFLICT, "BALANCE-003", "잔액 업데이트에 실패했습니다."),

    // 쿠폰 관련 에러
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON-001", "쿠폰을 찾을 수 없습니다."),
    COUPON_EXHAUSTED(HttpStatus.UNPROCESSABLE_ENTITY, "COUPON-002", "쿠폰이 모두 소진되었습니다."),
    COUPON_EXPIRED(HttpStatus.UNPROCESSABLE_ENTITY, "COUPON-003", "만료된 쿠폰입니다."),
    COUPON_ALREADY_USED(HttpStatus.UNPROCESSABLE_ENTITY, "COUPON-004", "이미 사용된 쿠폰입니다."),
    COUPON_ALREADY_ISSUED(HttpStatus.CONFLICT, "COUPON-005", "이미 발급받은 쿠폰입니다."),

    // 외부 시스템 연동 관련 에러
    EXTERNAL_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EXTERNAL-001", "외부 시스템 연동 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}