package com.trademate.trademate.auth.controller;

import com.trademate.trademate.auth.dto.LoginRequest;
import com.trademate.trademate.auth.dto.LoginResponse;
import com.trademate.trademate.auth.service.AuthService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Authentication API", description = "로그인 등 인증 관련 API")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
