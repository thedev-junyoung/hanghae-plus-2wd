package kr.hhplus.be.server.domain.balance.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.BalanceAPI;
import kr.hhplus.be.server.common.dto.response.CustomApiResponse;
import kr.hhplus.be.server.domain.balance.dto.request.ChargeBalanceRequest;
import kr.hhplus.be.server.domain.balance.dto.response.BalanceResponse;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController implements BalanceAPI {

    private final BalanceService balanceService;

    @Override
    public ResponseEntity<CustomApiResponse<BalanceResponse>> chargeBalance(@Valid ChargeBalanceRequest request) {
        BalanceResponse response = balanceService.chargeBalance(request);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CustomApiResponse<BalanceResponse>> getBalance(Long userId) {
        BalanceResponse response = balanceService.getBalance(userId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}