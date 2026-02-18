package com.trademate.trademate.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemCreateRequest {

    @NotBlank(message = "상품 제목은 필수입니다.")
    private String title;

    private String description;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;
}