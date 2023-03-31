package com.sixtythree.stock63.domestic.kospi.repository;

import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KospiItemRepository extends JpaRepository<KospiItem, String> {
    List<KospiItem> findTop100ByOrderByPrdyAvlsScalDesc();
    @Query(value = "select * from kospi_item order by CAST(prdy_avls_scal AS SIGNED) DESC", nativeQuery = true)
    List<KospiItem> findAllOrderByPrdyAvlsScalDesc2();
}
