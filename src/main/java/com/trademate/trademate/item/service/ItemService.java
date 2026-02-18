package com.trademate.trademate.item.service;

import com.trademate.trademate.common.exception.UnauthorizedException;
import com.trademate.trademate.domain.item.Item;
import com.trademate.trademate.domain.item.ItemRepository;
import com.trademate.trademate.domain.item.ItemStatus;
import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import com.trademate.trademate.item.dto.ItemCreateRequest;
import com.trademate.trademate.item.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemResponse createItem(Long sellerId, ItemCreateRequest req) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new UnauthorizedException("인증된 사용자를 찾을 수 없습니다."));

        Item item = Item.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .price(req.getPrice())
                .status(ItemStatus.SELLING)
                .seller(seller)
                .build();

        Item saved = itemRepository.save(item);
        return ItemResponse.from(saved);
    }
}