// API 인터페이스
package kr.hhplus.be.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.common.exception.ApiErrorResponse;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.payment.dto.request.ProcessPaymentRequest;
import kr.hhplus.be.server.domain.payment.dto.response.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 관리 API")
@RequestMapping("/api/v1/payments")
public interface PaymentAPI {

    @Operation(summary = "결제 처리", description = "주문에 대한 결제를 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "주문 또는 사용자 없음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "동시성 충돌",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "처리 불가능(잔액 부족)",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/process")
    ResponseEntity<CustomApiResponse<PaymentResponse>> processPayment(
            @Parameter(description = "결제 처리 요청", required = true)
            @Valid @RequestBody ProcessPaymentRequest request
    );

    @Operation(summary = "결제 상태 조회", description = "결제의 현재 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "결제 정보 없음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{paymentId}")
    ResponseEntity<CustomApiResponse<PaymentResponse>> getPaymentStatus(
            @Parameter(description = "결제 ID", example = "5678", required = true)
            @PathVariable Long paymentId
    );

    @Operation(summary = "주문별 결제 조회", description = "주문에 대한 결제 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "주문 없음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/order/{orderId}")
    ResponseEntity<CustomApiResponse<PaymentResponse>> getPaymentByOrderId(
            @Parameter(description = "주문 ID", example = "9876", required = true)
            @PathVariable Long orderId
    );
}