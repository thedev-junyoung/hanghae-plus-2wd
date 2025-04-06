package kr.hhplus.be.server.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인기 스니커즈 상품 정보")
public class PopularProductDTO extends ProductDTO {
    @Schema(description = "판매 수량", example = "240")
    private int salesCount;

    @Schema(description = "순위", example = "1")
    private int rank;

    @Schema(description = "인기도 점수", example = "95.8")
    private double popularityScore;

    @Schema(description = "재판매 가격 변동률(%)", example = "23.5")
    private double resellPriceChangeRate;

    @Schema(description = "재판매 마켓 평균가", example = "450000")
    private BigDecimal avgResellPrice;
}