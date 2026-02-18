package com.trademate.trademate.item.dto;

import com.trademate.trademate.domain.item.Item;
import com.trademate.trademate.domain.item.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemResponse {
    private Long id;
    private String title;
    private String description;
    private Integer price;
    private ItemStatus status;
    private Long sellerId;
    private LocalDateTime createdAt;

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice(),
                item.getStatus(),
                item.getSeller().getId(),
                item.getCreatedAt()
        );
    }
}