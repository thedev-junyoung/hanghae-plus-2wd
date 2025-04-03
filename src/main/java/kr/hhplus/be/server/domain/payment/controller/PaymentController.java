package kr.hhplus.be.server.domain.payment.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.PaymentAPI;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.payment.dto.request.ProcessPaymentRequest;
import kr.hhplus.be.server.domain.payment.dto.response.PaymentResponse;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentAPI {

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<CustomApiResponse<PaymentResponse>> processPayment(@Valid ProcessPaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<PaymentResponse>> confirmPayment(String pgTransactionId) {
        PaymentResponse response = paymentService.confirmPayment(pgTransactionId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<PaymentResponse>> getPaymentStatus(Long paymentId) {
        PaymentResponse response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<PaymentResponse>> getPaymentByOrderId(Long orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}