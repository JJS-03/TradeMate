package com.trademate.trademate.domain.item;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {


@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select i from Item i join fetch i.seller where i.id = :id")
Optional<Item> findByIdForUpdate(@Param("id") Long id);
}