package com.trademate.trademate.domain.trade;

import com.trademate.trademate.domain.item.Item;
import com.trademate.trademate.domain.item.ItemRepository;
import com.trademate.trademate.domain.item.ItemStatus;
import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TradeRepositoryTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("거래 생성 테스트")
    void createTrade() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User seller = userRepository.save(User.builder()
                .email("seller3-" + suffix + "@test.com")
                .password("password")
                .nickname("seller3")
                .build());

        User buyer = userRepository.save(User.builder()
                .email("buyer1-" + suffix + "@test.com")
                .password("password")
                .nickname("buyer1")
                .build());

        Item item = itemRepository.saveAndFlush(Item.builder()
                .title("Nintendo Switch")
                .description("OLED")
                .price(350000)
                .status(ItemStatus.RESERVED)
                .seller(seller)
                .build());

        Trade trade = tradeRepository.saveAndFlush(Trade.builder()
                .item(item)
                .seller(seller)
                .buyer(buyer)
                .status(TradeStatus.RESERVED)
                .build());

        Trade found = tradeRepository.findById(trade.getId()).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getStatus()).isEqualTo(TradeStatus.RESERVED);
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("거래-아이템-사용자 연관관계 검증 테스트")
    void verifyTradeRelationships() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User seller = userRepository.save(User.builder()
                .email("seller4-" + suffix + "@test.com")
                .password("password")
                .nickname("seller4")
                .build());

        User buyer = userRepository.save(User.builder()
                .email("buyer2-" + suffix + "@test.com")
                .password("password")
                .nickname("buyer2")
                .build());

        Item item = itemRepository.saveAndFlush(Item.builder()
                .title("PlayStation 5")
                .description("Disk Edition")
                .price(500000)
                .status(ItemStatus.SOLD)
                .seller(seller)
                .build());

        Trade trade = tradeRepository.saveAndFlush(Trade.builder()
                .item(item)
                .seller(seller)
                .buyer(buyer)
                .status(TradeStatus.COMPLETED)
                .build());

        Trade detailed = tradeRepository.findWithDetailsById(trade.getId()).orElseThrow();

        assertThat(detailed.getItem().getId()).isEqualTo(item.getId());
        assertThat(detailed.getSeller().getId()).isEqualTo(seller.getId());
        assertThat(detailed.getBuyer().getId()).isEqualTo(buyer.getId());
        assertThat(detailed.getItem().getStatus()).isEqualTo(ItemStatus.SOLD);
        assertThat(detailed.getStatus()).isEqualTo(TradeStatus.COMPLETED);
    }
}