package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.domain.balance.dto.request.ChargeBalanceRequest;
import kr.hhplus.be.server.domain.balance.dto.response.BalanceResponse;

public interface BalanceService {
    /**
     * 사용자 잔액을 충전합니다.
     */
    BalanceResponse chargeBalance(ChargeBalanceRequest request);

    /**
     * 사용자의 현재 잔액을 조회합니다.
     */
    BalanceResponse getBalance(Long userId);

    /**
     * 결제 시 잔액을 차감합니다. (결제 서비스에서 호출)
     * @return 차감 성공 여부
     */
    boolean decreaseBalance(Long userId, java.math.BigDecimal amount);
}