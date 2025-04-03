package kr.hhplus.be.server.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.common.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 목록 응답")
public class OrderListResponse {
    @Schema(description = "주문 목록")
    private List<OrderSummaryDTO> orders;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}

