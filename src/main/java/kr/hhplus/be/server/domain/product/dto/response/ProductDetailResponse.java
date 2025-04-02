package kr.hhplus.be.server.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 상세 응답")
public class ProductDetailResponse {

    @Schema(description = "상품 상세 정보")
    private ProductDTO product;
}
