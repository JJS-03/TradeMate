package com.trademate.trademate.domain.trade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    @EntityGraph(attributePaths = {"item", "seller", "buyer"})
    Optional<Trade> findWithDetailsById(Long id);

    @Query(
            value = "select t from Trade t join fetch t.item where t.buyer.id = :buyerId order by t.id desc",
            countQuery = "select count(t) from Trade t where t.buyer.id = :buyerId"
    )
    Page<Trade> findPurchasesByBuyerId(@Param("buyerId") Long buyerId, Pageable pageable);

    @Query(
            value = "select t from Trade t join fetch t.item where t.seller.id = :sellerId order by t.id desc",
            countQuery = "select count(t) from Trade t where t.seller.id = :sellerId"
    )
    Page<Trade> findSalesBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);
}