package com.sixtythree.stock63.domestic.kospi.repository;

import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KospiDailyPriceRepository extends JpaRepository<KospiDailyPrice, String> {

    List<KospiDailyPrice> findAllByStckBsopDateBetween(String startDate, String endDate);
}
