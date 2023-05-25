package com.sixtythree.stock63.domestic.kospi.repository;

import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KospiDailyPriceRepository extends JpaRepository<KospiDailyPrice, String> {

    List<KospiDailyPrice> findAllByStckBsopDateBetween(String startDate, String endDate);

    @Query(value = "select min(stck_bsop_date) as start_date, max(stck_bsop_date) as end_date" +
            " from (" +
            "select stck_bsop_date from domestic_db.kospi_daily_price " +
            "group by stck_bsop_date " +
            "order by stck_bsop_date desc " +
            "limit :day" +
            ") a",
            nativeQuery = true
    )
    Map<String, String> findStartEndDate(@Param("day") int day);

    @Query(value = "delete from kospi_daily_price where stck_bsop_date <= :deleteDate", nativeQuery = true)
    void deleteAllByStckBsopDate(@Param("deleteDate") String deleteDate);
}
