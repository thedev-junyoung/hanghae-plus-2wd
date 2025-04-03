package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.dto.request.CreateOrderRequest;
import kr.hhplus.be.server.domain.order.dto.response.OrderListResponse;
import kr.hhplus.be.server.domain.order.dto.response.OrderResponse;

public interface OrderService {
    /**
     * 주문을 생성합니다.
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * 주문을 조회합니다.
     */
    OrderResponse getOrder(Long orderId);

    /**
     * 사용자의 주문 목록을 조회합니다.
     */
    OrderListResponse getUserOrders(Long userId, int page, int size, String status);

    /**
     * 주문을 취소합니다.
     */
    OrderResponse cancelOrder(Long orderId, Long userId);
}