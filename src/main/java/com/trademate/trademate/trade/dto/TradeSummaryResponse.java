package com.trademate.trademate.trade.dto;

import com.trademate.trademate.domain.trade.Trade;
import com.trademate.trademate.domain.trade.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TradeSummaryResponse {
    private Long tradeId;
    private TradeStatus tradeStatus;
    private Long itemId;
    private String itemTitle;
    private Integer itemPrice;
    private LocalDateTime createdAt;

    public static TradeSummaryResponse from(Trade trade) {
        return new TradeSummaryResponse(
                trade.getId(),
                trade.getStatus(),
                trade.getItem().getId(),
                trade.getItem().getTitle(),
                trade.getItem().getPrice(),
                trade.getCreatedAt()
        );
    }
}