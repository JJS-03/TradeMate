package com.trademate.trademate.trade.controller;

import com.trademate.trademate.auth.jwt.JwtProvider;
import com.trademate.trademate.common.exception.UnauthorizedException;
import com.trademate.trademate.trade.dto.TradeResponse;
import com.trademate.trademate.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final TradeService tradeService;

    @PostMapping("/{tradeId}/complete")
    public ResponseEntity<TradeResponse> completeTrade(
            @PathVariable Long tradeId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        String token = extractToken(authorization);

        Long userId;
        try {
            userId = jwtProvider.getUserId(token);
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        TradeResponse response = tradeService.completeTrade(tradeId, userId);
        return ResponseEntity.ok(response);
    }

    private String extractToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new UnauthorizedException("Authorization 헤더가 필요합니다.");
        }

        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("Authorization 헤더 형식이 올바르지 않습니다.");
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            throw new UnauthorizedException("토큰이 비어 있습니다.");
        }
        return token;
    }
}