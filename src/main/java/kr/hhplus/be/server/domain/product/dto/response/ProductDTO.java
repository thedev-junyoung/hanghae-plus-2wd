package kr.hhplus.be.server.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 정보")
public class ProductDTO {
    @Schema(description = "상품 ID", example = "1001")
    private Long id;

    @Schema(description = "상품명", example = "맥북 프로")
    private String name;

    @Schema(description = "가격", example = "2990000")
    private BigDecimal price;

    @Schema(description = "재고 수량", example = "12")
    private Integer stockQuantity;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;
}
