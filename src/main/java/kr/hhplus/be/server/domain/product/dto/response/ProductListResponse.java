package kr.hhplus.be.server.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.common.dto.Pagination;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 목록 응답")
public class ProductListResponse {

    @Schema(description = "상품 목록")
    private List<ProductDTO> products;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
