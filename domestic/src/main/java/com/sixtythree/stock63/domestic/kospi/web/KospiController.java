package com.sixtythree.stock63.domestic.kospi.web;

import com.sixtythree.stock63.domestic.kospi.dto.StockDto;
import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sixtythree.stock63.domestic.kospi.service.KospiService;

import java.text.ParseException;
import java.util.List;

@Tag(name = "kospi", description = "국내주식 코스피")
@Controller
@RequestMapping("/domestic/kospi")
@RequiredArgsConstructor
public class KospiController {

    private final KospiService kospiService;

    @ResponseBody
    @GetMapping("list")
    public ResponseEntity<List<StockDto>> rankList(
            @Parameter(description = "연속 상승/하락 일수")
            @RequestParam(name ="period",required = true) int period,
            @Parameter(description = "상승: 1 / 하락: -1")
            @RequestParam(name="gradient",required = true) int gradient,
            @Parameter(description = "시가총액 / 양수: 이상, 음수: 이하")
            @RequestParam(name="avlsScal",required = false) int avlsScal
            ) {
        return kospiService.stockList(period, gradient, avlsScal);
    }

    @ResponseBody
    @GetMapping("pirce-by-period")
    public ResponseEntity<List<KospiDailyPrice>> priceByPeriod(
            @Parameter(description = "종목코드")
            @RequestParam(name="종목코드", required = true) String mkscShrnIscd,
            @Parameter(description = "기간분류코드 - D:일봉/W:주봉/M:월봉/Y:연봉")
            @RequestParam(name="기간분류코드", required = true) String periodDivCode
    ) throws ParseException {
        return kospiService.getPriceByPeriod(mkscShrnIscd, periodDivCode);
    }

    @ResponseBody
    @GetMapping("test")
    public String test(){
        return "test";
    }

}
