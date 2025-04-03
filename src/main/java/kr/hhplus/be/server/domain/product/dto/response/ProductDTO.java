package kr.hhplus.be.server.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스니커즈 상품 정보")
public class ProductDTO {
    @Schema(description = "상품 ID", example = "1001")
    private Long id;

    @Schema(description = "상품명", example = "Nike Air Jordan 1 Retro High OG")
    private String name;

    @Schema(description = "브랜드", example = "Nike")
    private String brand;

    @Schema(description = "가격", example = "299000")
    private BigDecimal price;

    @Schema(description = "원가", example = "250000")
    private BigDecimal originalPrice;

    @Schema(description = "재고 수량", example = "12")
    private Integer stockQuantity;

    @Schema(description = "출시일", example = "2025-01-15")
    private LocalDate releaseDate;

    @Schema(description = "가용 사이즈 목록", example = "[250, 255, 260, 265, 270, 275, 280]")
    private List<Integer> availableSizes;

    @Schema(description = "상품 상세 설명")
    private String description;

    @Schema(description = "상품 이미지 URL", example = "/images/products/aj1-chicago.jpg")
    private String imageUrl;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;
}
