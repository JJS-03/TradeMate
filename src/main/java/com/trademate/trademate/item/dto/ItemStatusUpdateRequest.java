package com.trademate.trademate.item.dto;

import com.trademate.trademate.domain.item.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemStatusUpdateRequest {

    @NotNull(message = "상태는 필수입니다.")
    private ItemStatus status;
}