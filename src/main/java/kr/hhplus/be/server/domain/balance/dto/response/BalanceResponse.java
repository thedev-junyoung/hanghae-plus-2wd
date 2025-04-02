package kr.hhplus.be.server.domain.balance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "잔액 응답")
public class BalanceResponse {

    @Schema(description = "사용자 ID", example = "12345")
    private Long userId;

    @Schema(description = "현재 잔액", example = "150000")
    private BigDecimal balance;

    @Schema(description = "잔액 갱신 시각", example = "2025-04-02T10:30:00")
    private LocalDateTime updatedAt;
}
