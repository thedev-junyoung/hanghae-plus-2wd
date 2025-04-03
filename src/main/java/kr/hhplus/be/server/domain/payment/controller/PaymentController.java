// 컨트롤러 구현체
package kr.hhplus.be.server.domain.payment.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.PaymentAPI;
import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.payment.dto.request.ProcessPaymentRequest;
import kr.hhplus.be.server.domain.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentAPI {

    private final AtomicLong paymentIdGenerator = new AtomicLong(5000L);

    // Mock 데이터 저장소
    private final Map<Long, PaymentResponse> paymentStore = new ConcurrentHashMap<>();
    private final Map<Long, PaymentResponse> orderPaymentStore = new ConcurrentHashMap<>();
    private final Map<Long, BigDecimal> userBalances = new ConcurrentHashMap<>(); // 실제로는 BalanceService를 주입받아 사용

    @Override
    public ResponseEntity<CustomApiResponse<PaymentResponse>> processPayment(@Valid ProcessPaymentRequest request) {
        // 주문 존재 여부 확인 (Mock)
        if (request.getOrderId() < 1) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 사용자 존재 여부 확인 (Mock)
        if (request.getUserId() < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 잔액 확인 (Mock)
        BigDecimal userBalance = userBalances.getOrDefault(request.getUserId(), BigDecimal.ZERO);
        if (userBalance.compareTo(request.getAmount()) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 잔액 차감 (Mock)
        userBalances.put(request.getUserId(), userBalance.subtract(request.getAmount()));

        // 결제 정보 생성
        Long paymentId = paymentIdGenerator.getAndIncrement();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(paymentId)
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status("SUCCESS")
                .method("BALANCE")
                .pgTransactionId(String.format("PG_%d_%d", request.getOrderId(), System.currentTimeMillis()))
                .createdAt(LocalDateTime.now())
                .build();

        // 결제 정보 저장
        paymentStore.put(paymentId, paymentResponse);
        orderPaymentStore.put(request.getOrderId(), paymentResponse);

        // TODO: 트랜잭셔널 아웃박스 패턴 - 결제 성공 이벤트를 ORDER_EVENTS 테이블에 저장
        // 실제 구현 시에는 OrderEventsRepository를 통해 이벤트 저장
        System.out.println("결제 성공 이벤트 저장: 주문 ID " + request.getOrderId());

        return ResponseEntity.ok(CustomApiResponse.success(paymentResponse));
    }

    @Override
    public ResponseEntity<CustomApiResponse<PaymentResponse>> getPaymentStatus(Long paymentId) {
        // 결제 정보 조회
        PaymentResponse payment = paymentStore.get(paymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        return ResponseEntity.ok(CustomApiResponse.success(payment));
    }

    @Override
    public ResponseEntity<CustomApiResponse<PaymentResponse>> getPaymentByOrderId(Long orderId) {
        // 주문별 결제 정보 조회
        PaymentResponse payment = orderPaymentStore.get(orderId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        return ResponseEntity.ok(CustomApiResponse.success(payment));
    }
}