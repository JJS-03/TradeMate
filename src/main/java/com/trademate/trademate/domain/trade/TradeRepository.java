package com.trademate.trademate.domain.trade;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    @EntityGraph(attributePaths = {"item", "seller", "buyer"})
    Optional<Trade> findWithDetailsById(Long id);
}