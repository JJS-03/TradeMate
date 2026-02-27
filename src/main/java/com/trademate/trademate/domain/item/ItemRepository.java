package com.trademate.trademate.domain.item;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(
            value = """
      select i
      from Item i
      join fetch i.seller
      where (:q is null or i.title like concat('%', :q, '%'))
        and (:status is null or i.status = :status)
      """,
            countQuery = """
      select count(i)
      from Item i
      where (:q is null or i.title like concat('%', :q, '%'))
        and (:status is null or i.status = :status)
      """
    )
    Page<Item> search(@Param("q") String q,
                      @Param("status") ItemStatus status,
                      Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i join fetch i.seller where i.id = :id")
    Optional<Item> findByIdForUpdate(@Param("id") Long id);
}