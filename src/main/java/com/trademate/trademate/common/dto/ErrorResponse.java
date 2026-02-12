package com.trademate.trademate.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;              // 예: VALIDATION_ERROR
    private String message;           // 예: 입력값이 올바르지 않습니다.
    private Map<String, String> errors; // 필드별 에러 (없으면 null 가능)
}
