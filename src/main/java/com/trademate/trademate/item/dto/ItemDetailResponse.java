package com.trademate.trademate.item.dto;

import com.trademate.trademate.domain.item.Item;
import com.trademate.trademate.domain.item.ItemStatus;
import com.trademate.trademate.domain.trade.Trade;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Integer price;
    private ItemStatus status;
    private Long sellerId;
    private LocalDateTime createdAt;
    private Long tradeId;
    private Long buyerId;

    public static ItemDetailResponse from(Item item, Trade reservedTrade) {
        return new ItemDetailResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice(),
                item.getStatus(),
                item.getSeller().getId(),
                item.getCreatedAt(),
                reservedTrade != null ? reservedTrade.getId() : null,
                reservedTrade != null ? reservedTrade.getBuyer().getId() : null
        );
    }
}