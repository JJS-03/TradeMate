package com.trademate.trademate.user.controller;

import com.trademate.trademate.auth.jwt.JwtProvider;
import com.trademate.trademate.common.exception.UnauthorizedException;
import com.trademate.trademate.user.dto.MeResponse;
import com.trademate.trademate.user.dto.SignUpRequest;
import com.trademate.trademate.user.dto.SignUpResponse;
import com.trademate.trademate.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("인증 헤더가 올바르지 않습니다.");
        }

        String token = authorization.substring(7).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException("토큰이 비어있습니다.");
        }

        Long userId;
        try {
            userId = jwtProvider.getUserId(token);
        } catch (RuntimeException e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        return ResponseEntity.ok(userService.getMe(userId));
    }

}