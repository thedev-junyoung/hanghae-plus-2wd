package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.payment.dto.request.ProcessPaymentRequest;
import kr.hhplus.be.server.domain.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class MockPaymentService implements PaymentService {

    private final AtomicLong paymentIdGenerator = new AtomicLong(5000L);
    private final Map<Long, PaymentResponse> paymentStore = new ConcurrentHashMap<>();
    private final Map<Long, PaymentResponse> orderPaymentStore = new ConcurrentHashMap<>();

    // 실제 구현에서는 BalanceService 의존성 주입
    private final BalanceService balanceService;

    @Override
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        // 주문 존재 여부 확인 (Mock)
        if (request.getOrderId() < 1) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 사용자 존재 여부 확인 (Mock)
        if (request.getUserId() < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // BalanceService를 통한 잔액 차감
        boolean decreased = balanceService.decreaseBalance(request.getUserId(), request.getAmount());
        if (!decreased) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

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
        System.out.println("결제 성공 이벤트 저장: 주문 ID " + request.getOrderId());

        return paymentResponse;
    }

    @Override
    public PaymentResponse confirmPayment(String pgTransactionId) {
        // 트랜잭션 ID로 결제 조회
        PaymentResponse payment = paymentStore.values().stream()
                .filter(p -> pgTransactionId.equals(p.getPgTransactionId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        // 이미 처리된 결제인지 확인
        if ("SUCCESS".equals(payment.getStatus())) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        // 결제 상태 업데이트
        PaymentResponse confirmedPayment = PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status("SUCCESS") // 결제 확인 완료
                .method(payment.getMethod())
                .pgTransactionId(payment.getPgTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();

        // 결제 정보 업데이트
        paymentStore.put(payment.getPaymentId(), confirmedPayment);
        orderPaymentStore.put(payment.getOrderId(), confirmedPayment);

        // TODO: 트랜잭셔널 아웃박스 패턴 - 결제 확인 이벤트를 ORDER_EVENTS 테이블에 저장
        System.out.println("결제 확인 이벤트 저장: 주문 ID " + payment.getOrderId());

        return confirmedPayment;
    }

    @Override
    public PaymentResponse getPaymentStatus(Long paymentId) {
        // 결제 정보 조회
        PaymentResponse payment = paymentStore.get(paymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        // 주문별 결제 정보 조회
        PaymentResponse payment = orderPaymentStore.get(orderId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }
}