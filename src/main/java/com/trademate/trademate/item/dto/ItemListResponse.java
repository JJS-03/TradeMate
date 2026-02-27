package com.trademate.trademate.item.dto;

import com.trademate.trademate.domain.item.Item;
import com.trademate.trademate.domain.item.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ItemListResponse {

    private Long id;
    private String title;
    private Integer price;
    private ItemStatus status;
    private Long sellerId;
    private LocalDateTime createdAt;

    public static ItemListResponse from(Item item) {
        return ItemListResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .price(item.getPrice())
                .status(item.getStatus())
                .sellerId(item.getSeller().getId())
                .createdAt(item.getCreatedAt())
                .build();
    }
}