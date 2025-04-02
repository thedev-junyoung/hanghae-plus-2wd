package kr.hhplus.be.server.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.dto.request.OrderRequestDto;
import kr.hhplus.be.server.dto.request.OrderResponseDto;
import kr.hhplus.be.server.dto.response.ApiResponse;
import kr.hhplus.be.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "주문", description = "주문 관련 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "상품과 쿠폰 정보를 입력받아 주문을 생성하고 결제를 수행합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(@RequestBody OrderRequestDto requestDto) {
        OrderResponseDto response = orderService.createOrder(requestDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID를 통해 주문 상세 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(@PathVariable Long orderId) {
        OrderResponseDto response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}