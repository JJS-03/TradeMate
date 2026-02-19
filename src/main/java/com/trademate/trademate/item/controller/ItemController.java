package com.trademate.trademate.item.controller;

import com.trademate.trademate.auth.jwt.JwtProvider;
import com.trademate.trademate.common.exception.UnauthorizedException;
import com.trademate.trademate.item.dto.ItemCreateRequest;
import com.trademate.trademate.item.dto.ItemResponse;
import com.trademate.trademate.item.dto.ItemUpdateRequest;
import com.trademate.trademate.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ItemCreateRequest request
    ) {
        String token = extractToken(authorization);

        Long sellerId;
        try {
            sellerId = jwtProvider.getUserId(token);
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        ItemResponse response = itemService.createItem(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ItemUpdateRequest request
    ) {
        String token = extractToken(authorization);

        Long userId;
        try {
            userId = jwtProvider.getUserId(token);
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        ItemResponse response = itemService.updateItem(itemId, userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        String token = extractToken(authorization);

        Long userId;
        try {
            userId = jwtProvider.getUserId(token);
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        itemService.deleteItem(itemId, userId);
        return ResponseEntity.noContent().build();
    }

    private String extractToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new UnauthorizedException("Authorization 헤더가 필요합니다.");
        }

        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("Authorization 헤더 형식이 올바르지 않습니다.");
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            throw new UnauthorizedException("토큰이 비어 있습니다.");
        }
        return token;
    }
}