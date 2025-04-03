package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.common.exception.BusinessException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.balance.dto.request.ChargeBalanceRequest;
import kr.hhplus.be.server.domain.balance.dto.response.BalanceResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MockBalanceService implements BalanceService {

    // Mock 데이터 저장소 - 실제 구현에서는 Repository로 대체됨
    private final Map<Long, BigDecimal> userBalances = new ConcurrentHashMap<>();

    // Mock 사용자 저장소 - 실제 구현에서는 UserRepository로 대체됨
    private final Set<Long> existingUsers = new HashSet<>();

    // 생성자에서 초기 데이터 설정
    public MockBalanceService() {
        // 테스트용 사용자 ID를 미리 등록
        existingUsers.add(1L);
        existingUsers.add(100L);
        existingUsers.add(200L);

        // 초기 잔액은 모두 0으로 설정 - 테스트에서 명시적으로 충전할 것임
        userBalances.put(1L, BigDecimal.ZERO);
        userBalances.put(100L, BigDecimal.ZERO);
        userBalances.put(200L, BigDecimal.ZERO);
    }

    @Override
    public BalanceResponse chargeBalance(ChargeBalanceRequest request) {
        // 사용자 존재 여부 확인
        validateUserExists(request.getUserId());

        // 금액 유효성 검증
        validateAmount(request.getAmount());

        // 현재 잔액 조회
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
        // 사용자 존재 여부 확인
        validateUserExists(userId);

        // 잔액 조회
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
        // 사용자 존재 여부 확인
        validateUserExists(userId);

        // 금액 유효성 검증
        validateAmount(amount);

        // 현재 잔액 조회
        BigDecimal currentBalance = userBalances.getOrDefault(userId, BigDecimal.ZERO);

        // 잔액 부족 확인
        if (currentBalance.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 잔액 차감
        BigDecimal newBalance = currentBalance.subtract(amount);
        userBalances.put(userId, newBalance);

        return true;
    }

    // 테스트용 헬퍼 메서드: 잔액 초기화
    public void resetBalance(Long userId, BigDecimal initialBalance) {
        if (existingUsers.contains(userId)) {
            userBalances.put(userId, initialBalance);
        }
    }

    // 검증 헬퍼 메서드들
    private void validateUserExists(Long userId) {
        if (userId == null || userId < 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER);
        }

        // 특정 사용자 ID만 존재한다고 가정하고 나머지는 404 반환
        if (!existingUsers.contains(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
    }

    // 내부에 자동 등록 보조 메서드 추가
    public void registerUserIfNotExists(Long userId, BigDecimal initialBalance) {
        if (!existingUsers.contains(userId)) {
            existingUsers.add(userId);
            userBalances.put(userId, initialBalance);
        }
    }
}