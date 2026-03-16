package com.trademate.trademate.domain.item;

import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("아이템 생성 테스트")
    void createItem() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User seller = userRepository.save(User.builder()
                .email("seller1-" + suffix + "@test.com")
                .password("password")
                .nickname("seller1")
                .build());

        Item item = itemRepository.saveAndFlush(Item.builder()
                .title("MacBook Pro")
                .description("M3 chip")
                .price(2500000)
                .status(ItemStatus.SELLING)
                .seller(seller)
                .build());

        Item found = itemRepository.findById(item.getId()).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getStatus()).isEqualTo(ItemStatus.SELLING);
        assertThat(found.getSeller().getId()).isEqualTo(seller.getId());
    }

    @Test
    @DisplayName("아이템-판매자 연관관계 및 PESSIMISTIC_WRITE 설정 검증")
    void verifyItemRelationshipAndPessimisticWriteConfiguration() throws NoSuchMethodException {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User seller = userRepository.save(User.builder()
                .email("seller2-" + suffix + "@test.com")
                .password("password")
                .nickname("seller2")
                .build());

        Item item = itemRepository.saveAndFlush(Item.builder()
                .title("iPad Pro")
                .description("11 inch")
                .price(1200000)
                .status(ItemStatus.RESERVED)
                .seller(seller)
                .build());

        Item detail = itemRepository.findDetailById(item.getId()).orElseThrow();

        Method lockMethod = ItemRepository.class.getMethod("findByIdForUpdate", Long.class);
        Lock lockAnnotation = lockMethod.getAnnotation(Lock.class);

        assertThat(detail.getSeller().getEmail()).isEqualTo("seller2-" + suffix + "@test.com");
        assertThat(detail.getStatus()).isEqualTo(ItemStatus.RESERVED);
        assertThat(lockAnnotation).isNotNull();
        assertThat(lockAnnotation.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }
}