package com.trademate.trademate.item.service;

import com.trademate.trademate.common.exception.ForbiddenException;
import com.trademate.trademate.common.exception.NotFoundException;
import com.trademate.trademate.common.exception.UnauthorizedException;
import com.trademate.trademate.domain.item.Item;
import com.trademate.trademate.domain.item.ItemRepository;
import com.trademate.trademate.domain.item.ItemStatus;
import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import com.trademate.trademate.item.dto.ItemCreateRequest;
import com.trademate.trademate.item.dto.ItemResponse;
import com.trademate.trademate.item.dto.ItemUpdateRequest;
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
    public ItemResponse updateItem(Long itemId, Long userId, ItemUpdateRequest req) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));

        if (!item.getSeller().getId().equals(userId)) {
            throw new ForbiddenException("본인이 등록한 상품만 수정할 수 있습니다.");
        }

        item.update(req.getTitle(), req.getDescription(), req.getPrice());

        return ItemResponse.from(item);
    }

    public ItemResponse changeStatus(Long itemId, Long userId, ItemStatus newStatus) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));

        if (!item.getSeller().getId().equals(userId)) {
            throw new ForbiddenException("본인이 등록한 상품만 상태를 변경할 수 있습니다.");
        }

        if (newStatus == null) throw new IllegalArgumentException("status는 필수입니다.");

        ItemStatus currentStatus = item.getStatus();
        if (currentStatus == ItemStatus.SOLD) {
            throw new IllegalArgumentException("판매 완료된 상품은 상태를 변경할 수 없습니다.");
        }

        boolean validTransition = (currentStatus == ItemStatus.SELLING && newStatus == ItemStatus.RESERVED)
                || (currentStatus == ItemStatus.RESERVED && newStatus == ItemStatus.SOLD);

        if (!validTransition) {
            throw new IllegalArgumentException("허용되지 않는 상품 상태 변경입니다.");
        }

        item.changeStatus(newStatus);
        return ItemResponse.from(item);
    }

    public void deleteItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));

        if (!item.getSeller().getId().equals(userId)) {
            throw new ForbiddenException("본인이 등록한 상품만 삭제할 수 있습니다.");
        }

        itemRepository.delete(item);
    }
}
