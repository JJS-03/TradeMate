package com.trademate.trademate.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemUpdateRequest {

    @NotBlank(message = "상품 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "상품 설명은 필수입니다.")
    private String description;

    @Positive(message = "가격은 0보다 커야 합니다.")
    private Integer price;
}