package kr.hhplus.be.server.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인기 상품 응답")
public class PopularProductResponse {

    @Schema(description = "인기 상품 목록")
    private List<PopularProductDTO> products;

    @Schema(description = "집계 시작 일시", example = "2025-03-30T00:00:00Z")
    private String periodStart;

    @Schema(description = "집계 종료 일시", example = "2025-04-02T23:59:59Z")
    private String periodEnd;
}
