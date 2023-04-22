package com.sixtythree.stock63.domestic.kospi.repository;

import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KospiDailyPriceRepository extends JpaRepository<KospiDailyPrice, String> {

    List<KospiDailyPrice> findAllByStckBsopDateBetween(String startDate, String endDate);

    @Query(value = "delete from kospi_daily_price where stck_bsop_date <= :deleteDate", nativeQuery = true)
    void deleteAllByStckBsopDate(@Param("deleteDate") String deleteDate);
}
