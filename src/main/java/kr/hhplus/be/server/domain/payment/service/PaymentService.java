package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.dto.request.ProcessPaymentRequest;
import kr.hhplus.be.server.domain.payment.dto.response.PaymentResponse;

public interface PaymentService {
    /**
     * 주문에 대한 결제를 처리합니다.
     */
    PaymentResponse processPayment(ProcessPaymentRequest request);

    /**
     * PG사로부터 전달받은 결제 확인 정보를 처리합니다.
     */
    PaymentResponse confirmPayment(String pgTransactionId);

    /**
     * 결제 ID로 결제 상태를 조회합니다.
     */
    PaymentResponse getPaymentStatus(Long paymentId);

    /**
     * 주문 ID로 해당 주문의 결제 정보를 조회합니다.
     */
    PaymentResponse getPaymentByOrderId(Long orderId);
}