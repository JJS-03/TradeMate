package com.trademate.trademate.trade.service;

import com.trademate.trademate.common.exception.ForbiddenException;
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

        Trade savedTrade = tradeRepository.saveAndFlush(trade);
        return TradeResponse.from(savedTrade);
    }
    public TradeResponse completeTrade(Long tradeId, Long userId) {
        Trade trade = tradeRepository.findWithDetailsById(tradeId)
                .orElseThrow(() -> new NotFoundException("거래를 찾을 수 없습니다."));

        if (!trade.getSeller().getId().equals(userId)) {
            throw new ForbiddenException("판매자만 거래를 완료할 수 있습니다.");
        }

        if (trade.getStatus() != TradeStatus.RESERVED) {
            throw new IllegalArgumentException("예약 상태의 거래만 완료할 수 있습니다.");
        }

        Item item = trade.getItem();
        if (item.getStatus() != ItemStatus.RESERVED) {
            throw new IllegalArgumentException("예약 상태의 상품만 판매 완료할 수 있습니다.");
        }

        trade.changeStatus(TradeStatus.COMPLETED);
        item.changeStatus(ItemStatus.SOLD);

        return TradeResponse.from(trade);
    }
    public TradeResponse cancelTrade(Long tradeId, Long userId) {
        Trade trade = tradeRepository.findWithDetailsById(tradeId)
                .orElseThrow(() -> new NotFoundException("거래를 찾을 수 없습니다."));

        if (!trade.getBuyer().getId().equals(userId)) {
            throw new ForbiddenException("구매자만 거래를 취소할 수 있습니다.");
        }

        if (trade.getStatus() != TradeStatus.RESERVED) {
            throw new IllegalArgumentException("예약 상태의 거래만 취소할 수 있습니다.");
        }

        Item item = trade.getItem();
        if (item.getStatus() != ItemStatus.RESERVED) {
            throw new IllegalArgumentException("예약 상태의 상품만 취소할 수 있습니다.");
        }

        trade.changeStatus(TradeStatus.CANCELED);
        item.changeStatus(ItemStatus.SELLING);

        return TradeResponse.from(trade);
    }
}