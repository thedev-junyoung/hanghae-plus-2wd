package kr.hhplus.be.server.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 상품 정보")
public class OrderItemDTO {
    @Schema(description = "상품 ID", example = "1001")
    private Long productId;

    @Schema(description = "상품명", example = "Nike Air Jordan 1 Retro High OG")
    private String productName;

    @Schema(description = "브랜드", example = "Nike")
    private String brand;

    @Schema(description = "선택한 사이즈", example = "270")
    private Integer size;

    @Schema(description = "수량", example = "1")
    private int quantity;

    @Schema(description = "단가", example = "299000")
    private BigDecimal price;

    @Schema(description = "합계", example = "299000")
    private BigDecimal amount;

    @Schema(description = "상품 이미지 URL", example = "/images/products/aj1-chicago.jpg")
    private String imageUrl;
}


