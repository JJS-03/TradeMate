package com.trademate.trademate.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String email;
    private String accessToken;
}

