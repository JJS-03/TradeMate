package com.trademate.trademate.trade.service;

import com.trademate.trademate.common.exception.NotFoundException;
import com.trademate.trademate.domain.item.Item;
import com.trademate.trademate.domain.item.ItemRepository;
import com.trademate.trademate.domain.item.ItemStatus;
import com.trademate.trademate.domain.trade.Trade;
import com.trademate.trademate.domain.trade.TradeRepository;
import com.trademate.trademate.domain.trade.TradeStatus;
import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import com.trademate.trademate.trade.dto.TradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;

    public TradeResponse purchase(Long itemId, Long buyerId) {
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));

        if (item.getStatus() != ItemStatus.SELLING) {
            throw new IllegalArgumentException("판매중인 상품만 구매할 수 있습니다.");
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new NotFoundException("구매자를 찾을 수 없습니다."));

        Long sellerId = item.getSeller().getId();
        if (sellerId.equals(buyerId)) {
            throw new IllegalArgumentException("자신이 등록한 상품은 구매할 수 없습니다.");
        }

        Trade trade = Trade.builder()
                .item(item)
                .seller(item.getSeller())
                .buyer(buyer)
                .status(TradeStatus.RESERVED)
                .build();

        item.changeStatus(ItemStatus.RESERVED);

        Trade savedTrade = tradeRepository.save(trade);
        return TradeResponse.from(savedTrade);
    }
}