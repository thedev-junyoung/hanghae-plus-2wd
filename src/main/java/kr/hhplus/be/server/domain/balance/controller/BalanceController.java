package kr.hhplus.be.server.domain.balance.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.BalanceAPI;
import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.response.CustomApiResponse;
import kr.hhplus.be.server.domain.balance.dto.request.ChargeBalanceRequest;
import kr.hhplus.be.server.domain.balance.dto.response.BalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class BalanceController implements BalanceAPI {

    // Mock 데이터 저장소
    private final Map<Long, BigDecimal> userBalances = new ConcurrentHashMap<>();

    @Override
    public ResponseEntity<CustomApiResponse<BalanceResponse>> chargeBalance(@Valid ChargeBalanceRequest request) {
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
        BalanceResponse balanceResponse = BalanceResponse.builder()
                .userId(request.getUserId())
                .balance(newBalance)
                .updatedAt(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(CustomApiResponse.success(balanceResponse));
    }

    @Override
    public ResponseEntity<CustomApiResponse<BalanceResponse>> getBalance(Long userId) {
        // 사용자 존재 여부 확인 (Mock)
        if (userId < 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 잔액 조회 (없으면 0으로 초기화)
        BigDecimal balance = userBalances.getOrDefault(userId, BigDecimal.ZERO);

        // 응답 생성
        BalanceResponse balanceResponse = BalanceResponse.builder()
                .userId(userId)
                .balance(balance)
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(balanceResponse));
    }
}