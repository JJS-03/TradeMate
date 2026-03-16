package com.trademate.trademate.trade.service;

import com.trademate.trademate.domain.item.Item;
import com.trademate.trademate.domain.item.ItemRepository;
import com.trademate.trademate.domain.item.ItemStatus;
import com.trademate.trademate.domain.trade.TradeRepository;
import com.trademate.trademate.domain.user.User;
import com.trademate.trademate.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TradeServicePessimisticLockConcurrencyTest {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @BeforeEach
    void setUp() {
        tradeRepository.deleteAllInBatch();
        itemRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동시에 같은 상품을 구매하면 비관적 락으로 1명만 성공한다")
    void purchaseConcurrently_onlyOneBuyerSucceeds() throws InterruptedException {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        User seller = userRepository.save(User.builder()
                .email("seller-lock-" + suffix + "@test.com")
                .password("password")
                .nickname("seller-lock")
                .build());

        User buyer1 = userRepository.save(User.builder()
                .email("buyer1-lock-" + suffix + "@test.com")
                .password("password")
                .nickname("buyer1-lock")
                .build());

        User buyer2 = userRepository.save(User.builder()
                .email("buyer2-lock-" + suffix + "@test.com")
                .password("password")
                .nickname("buyer2-lock")
                .build());

        Item item = itemRepository.saveAndFlush(Item.builder()
                .title("동시성 테스트 상품")
                .description("PESSIMISTIC_WRITE 검증")
                .price(10000)
                .status(ItemStatus.SELLING)
                .seller(seller)
                .build());

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Runnable buyer1Task = createPurchaseTask(item.getId(), buyer1.getId(), readyLatch, startLatch, doneLatch, successCount, failCount);
        Runnable buyer2Task = createPurchaseTask(item.getId(), buyer2.getId(), readyLatch, startLatch, doneLatch, successCount, failCount);

        executorService.submit(buyer1Task);
        executorService.submit(buyer2Task);

        boolean ready = readyLatch.await(5, TimeUnit.SECONDS);
        assertThat(ready)
                .as("두 구매 스레드가 시작 준비를 제한 시간 내에 완료해야 합니다.")
                .isTrue();

        startLatch.countDown();

        boolean completed = doneLatch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        assertThat(completed)
                .as("두 구매 요청이 제한 시간 내에 종료되어야 합니다.")
                .isTrue();

        assertThat(successCount.get())
                .as("동시 구매 시 정확히 1명만 구매 성공해야 합니다.")
                .isEqualTo(1);

        assertThat(failCount.get())
                .as("동시 구매 시 나머지 1명은 반드시 구매 실패해야 합니다.")
                .isEqualTo(1);

        assertThat(tradeRepository.count())
                .as("동시 구매 후 trades 테이블에는 거래가 정확히 1건만 저장되어야 합니다.")
                .isEqualTo(1L);

        Item foundItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(foundItem.getStatus())
                .as("첫 구매 성공 이후 상품 상태는 RESERVED 이어야 합니다.")
                .isEqualTo(ItemStatus.RESERVED);
    }

    private Runnable createPurchaseTask(Long itemId,
                                        Long buyerId,
                                        CountDownLatch readyLatch,
                                        CountDownLatch startLatch,
                                        CountDownLatch doneLatch,
                                        AtomicInteger successCount,
                                        AtomicInteger failCount) {
        return () -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                tradeService.purchase(itemId, buyerId);
                successCount.incrementAndGet();
            } catch (Exception ignored) {
                failCount.incrementAndGet();
            } finally {
                doneLatch.countDown();
            }
        };
    }
}