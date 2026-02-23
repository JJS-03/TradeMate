package com.trademate.trademate.trade.dto;

import com.trademate.trademate.domain.trade.Trade;
import com.trademate.trademate.domain.trade.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TradeResponse {
    private Long id;
    private Long itemId;
    private Long sellerId;
    private Long buyerId;
    private TradeStatus status;
    private LocalDateTime createdAt;

    public static TradeResponse from(Trade trade) {
        return new TradeResponse(
                trade.getId(),
                trade.getItem().getId(),
                trade.getSeller().getId(),
                trade.getBuyer().getId(),
                trade.getStatus(),
                trade.getCreatedAt()
        );
    }
}