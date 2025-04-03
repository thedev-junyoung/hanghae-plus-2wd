package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.balance.service.MockBalanceService;
import kr.hhplus.be.server.domain.payment.dto.request.ProcessPaymentRequest;
import kr.hhplus.be.server.domain.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private final BalanceService balanceService;

    @Override
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        // [INITIATED] → [VALIDATING]
        if (request.getOrderId() == null || request.getOrderId() < 1) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        if (request.getUserId() == null || request.getUserId() < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // [VALIDATING] → [PROCESSING] → [LOCK_ACQUIRED] → [DEBITED] or [FAILED]
        try {
            boolean decreased = balanceService.decreaseBalance(request.getUserId(), request.getAmount());
            if (!decreased) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
            }
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.USER_NOT_FOUND
                    && balanceService instanceof MockBalanceService mockBalanceService) {
                // fallback: 사용자 자동 등록
                mockBalanceService.registerUserIfNotExists(request.getUserId(), request.getAmount().add(BigDecimal.valueOf(10000)));
                boolean decreased = balanceService.decreaseBalance(request.getUserId(), request.getAmount());
                if (!decreased) {
                    throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
                }
            } else {
                throw ex;
            }
        }

        // [DEBITED] → [GATEWAY_REQUESTED] → [COMPLETED] (바로 성공 처리)
        Long paymentId = paymentIdGenerator.getAndIncrement();
        PaymentResponse payment = PaymentResponse.builder()
                .paymentId(paymentId)
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status("SUCCESS") // 바로 성공 처리로 변경
                .method("BALANCE")
                .pgTransactionId("PG_" + request.getOrderId() + "_" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .build();

        paymentStore.put(paymentId, payment);
        orderPaymentStore.put(request.getOrderId(), payment);

        System.out.println("[결제 성공] 주문 ID: " + request.getOrderId());
        return payment;
    }

    @Override
    public PaymentResponse confirmPayment(String pgTransactionId) {
        PaymentResponse payment = paymentStore.values().stream()
                .filter(p -> pgTransactionId.equals(p.getPgTransactionId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if ("SUCCESS".equals(payment.getStatus())) {
            return payment; // 재확인 허용
        }

        PaymentResponse updated = PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status("SUCCESS")
                .method(payment.getMethod())
                .pgTransactionId(payment.getPgTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();

        paymentStore.put(updated.getPaymentId(), updated);
        orderPaymentStore.put(updated.getOrderId(), updated);

        System.out.println("[결제 확인] 주문 ID: " + updated.getOrderId());
        return updated;
    }

    @Override
    public PaymentResponse getPaymentStatus(Long paymentId) {
        PaymentResponse payment = paymentStore.get(paymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        PaymentResponse payment = orderPaymentStore.get(orderId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }
}
