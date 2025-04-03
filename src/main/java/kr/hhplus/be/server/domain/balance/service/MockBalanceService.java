package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.balance.dto.request.ChargeBalanceRequest;
import kr.hhplus.be.server.domain.balance.dto.response.BalanceResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MockBalanceService implements BalanceService {

    // Mock 데이터 저장소
    private final Map<Long, BigDecimal> userBalances = new ConcurrentHashMap<>();

    @Override
    public BalanceResponse chargeBalance(ChargeBalanceRequest request) {
        // 사용자 존재 여부 확인 (Mock)
        if (request.getUserId() < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 충전 금액 유효성 검사
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }

        // 현재 잔액 조회 (없으면 0으로 초기화)
        BigDecimal currentBalance = userBalances.getOrDefault(request.getUserId(), BigDecimal.ZERO);

        // 잔액 충전
        BigDecimal newBalance = currentBalance.add(request.getAmount());
        userBalances.put(request.getUserId(), newBalance);

        // 응답 생성
        return BalanceResponse.builder()
                .userId(request.getUserId())
                .balance(newBalance)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public BalanceResponse getBalance(Long userId) {
        // 사용자 존재 여부 확인 (Mock)
        if (userId < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 잔액 조회 (없으면 0으로 초기화)
        BigDecimal balance = userBalances.getOrDefault(userId, BigDecimal.ZERO);

        // 응답 생성
        return BalanceResponse.builder()
                .userId(userId)
                .balance(balance)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public boolean decreaseBalance(Long userId, BigDecimal amount) {
        // 사용자 존재 여부 확인 (Mock)
        if (userId < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 금액 유효성 검사
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }

        // 현재 잔액 조회 (없으면 0으로 초기화)
        BigDecimal currentBalance = userBalances.getOrDefault(userId, BigDecimal.ZERO);

        // 잔액 확인
        if (currentBalance.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 잔액 차감
        BigDecimal newBalance = currentBalance.subtract(amount);
        userBalances.put(userId, newBalance);

        return true;
    }
}