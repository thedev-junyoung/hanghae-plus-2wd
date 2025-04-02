package kr.hhplus.be.server.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인기 상품 정보")
public class PopularProductDTO extends ProductDTO {
    @Schema(description = "판매 수량", example = "240")
    private int salesCount;

    @Schema(description = "순위", example = "1")
    private int rank;
}
