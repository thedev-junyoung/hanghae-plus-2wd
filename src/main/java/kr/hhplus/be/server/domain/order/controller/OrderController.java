package kr.hhplus.be.server.domain.order.controller;

import kr.hhplus.be.server.api.OrderAPI;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.order.dto.request.CreateOrderRequest;
import kr.hhplus.be.server.domain.order.dto.response.OrderListResponse;
import kr.hhplus.be.server.domain.order.dto.response.OrderResponse;
import kr.hhplus.be.server.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderAPI {

    private final OrderService orderService;

    @Override
    public ResponseEntity<CustomApiResponse<OrderResponse>> createOrder(CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<OrderResponse>> getOrder(Long orderId) {
        OrderResponse response = orderService.getOrder(orderId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<OrderListResponse>> getUserOrders(Long userId, int page, int size, String status) {
        OrderListResponse response = orderService.getUserOrders(userId, page, size, status);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<OrderResponse>> cancelOrder(Long orderId, Long userId) {
        OrderResponse response = orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}