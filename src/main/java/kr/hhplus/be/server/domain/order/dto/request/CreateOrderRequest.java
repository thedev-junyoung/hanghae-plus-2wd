package kr.hhplus.be.server.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 생성 요청")
public class CreateOrderRequest {
    @NotNull(message = "사용자 ID는 필수입니다.")
    @Schema(description = "사용자 ID", example = "12345")
    private Long userId;

    @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
    @Valid
    @Schema(description = "주문 상품 목록")
    private List<OrderItemRequest> items;

    @Schema(description = "적용할 쿠폰 ID")
    private Long userCouponId;

    @Schema(description = "배송 주소 정보")
    private ShippingAddress shippingAddress;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주문 상품 요청")
    public static class OrderItemRequest {
        @NotNull(message = "상품 ID는 필수입니다.")
        @Schema(description = "상품 ID", example = "1001")
        private Long productId;

        @NotNull(message = "상품 수량은 필수입니다.")
        @Schema(description = "상품 수량", example = "1")
        private Integer quantity;

        @NotNull(message = "상품 사이즈는 필수입니다.")
        @Schema(description = "선택한 사이즈", example = "270")
        private Integer size;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "배송 주소 정보")
    public static class ShippingAddress {
        @Schema(description = "받는 사람", example = "홍길동")
        private String receiverName;

        @Schema(description = "연락처", example = "010-1234-5678")
        private String phoneNumber;

        @Schema(description = "우편번호", example = "06164")
        private String zipCode;

        @Schema(description = "기본 주소", example = "서울시 강남구 테헤란로 123")
        private String address1;

        @Schema(description = "상세 주소", example = "456동 789호")
        private String address2;

        @Schema(description = "배송 메모", example = "부재시 경비실에 맡겨주세요")
        private String memo;
    }
}
