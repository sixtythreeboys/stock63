package com.sixtythree.stock63.domestic.kospi.service;

import com.sixtythree.stock63.domestic.kospi.dto.StockDto;
import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import com.sixtythree.stock63.domestic.kospi.repository.KospiDailyPriceRepository;
import com.sixtythree.stock63.domestic.kospi.repository.KospiItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KospiService {

    private final KospiItemRepository kospiItemRepository;
    private final KospiDailyPriceRepository kospiDailyPriceRepository;

    public ResponseEntity<List<StockDto>> stockList(int period, int gradient) {
        List<KospiItem> kospiItems = kospiItemRepository.findAllOrderByPrdyAvlsScalDesc2();
        Map<String, KospiDailyPrice[]> dailyInfoMap = new HashMap<>();
        for (KospiItem kospiItem : kospiItems) {
            dailyInfoMap.put(kospiItem.getMkscShrnIscd(), new KospiDailyPrice[period+1]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        String endDate = sdf.format(cal.getTime());
        cal.add(Calendar.DATE, -period);
        String startDate = sdf.format(cal.getTime());
        // startDate부터 endDate까지 일별주식 가격select
        List<KospiDailyPrice> kdpList = kospiDailyPriceRepository.findAllByStckBsopDateBetween(startDate, endDate);
        for (KospiDailyPrice kdp : kdpList) {
            int diffsDay = getTimeDays(kdp.getStckBsopDate()) - getTimeDays(startDate);
//            dailyInfoMap.get(kdp.getMkscShrnIscd())[diffsDay] = Integer.parseInt(kdp.getStckClpr());
            dailyInfoMap.get(kdp.getMkscShrnIscd())[diffsDay] = kdp;
        }
        //리턴값
        List<StockDto> result = new ArrayList<>();
        //상승
        if (gradient > 0) {
            for (KospiItem kospiItem : kospiItems) {
                // 종목일별리스트를 불러온다.
                KospiDailyPrice[] arr = dailyInfoMap.get(kospiItem.getMkscShrnIscd());
                // 첫 기준값 설정 / 값이 비어있는경우(휴장 or 누락) 스킵한다.
                int idx = 0;
                while (idx <= period && arr[idx] == null) {
                    idx++;
                }
                int pivot, start;
                KospiDailyPrice last = new KospiDailyPrice();
                if (idx < period) {
                    pivot = Integer.parseInt(arr[idx++].getStckClpr());
                    start = pivot;
                } else {
                    continue;
                }
                // period만큼 연속 상승인지 확인. / 값이 비어있는 경우 스킵
                boolean isTrue = true;
                for (int i=idx; i<=period; i++) {
                    if (arr[idx] == null) continue;
                    // 연속 상승이 아닌경우 false
                    if (pivot >= Integer.parseInt(arr[i].getStckClpr())) {
                        isTrue = false;
                        break;
                    }
                    pivot = Integer.parseInt(arr[i].getStckClpr());
                    last = arr[i];
                }
                if (isTrue) {
                    double totalCtrt =Math.round(((double)(pivot - start) / pivot)*100)/100.0;
                    double prdyCtrt = Math.round(((double)Integer.parseInt(last.getPrdyVrss()) / Integer.parseInt(last.getStckClpr()))*100)/100.0;
                    StockDto stockDto = new StockDto(
                            kospiItem.getMkscShrnIscd(),
                            kospiItem.getHtsKorIsnm(),
                            pivot,
                            kospiItem.getPrdyAvlsScal(),
                            prdyCtrt,
                            totalCtrt
                            );
                    result.add(stockDto);
                }
            }
        //하락
        } else if (gradient < 0) {
            for (KospiItem kospiItem : kospiItems) {
                // 휴장일은 모두 제외함.
                // period : 휴장일을 포함한 일수
                // period보다는 날짜 형식으로 요청을 받는게 좋을 것 같다.(휴장일을 모두 고려하기 어려움)
                KospiDailyPrice[] arr = dailyInfoMap.get(kospiItem.getMkscShrnIscd());
                int idx = 0;
                while (idx <= period && arr[idx] == null) {
                    idx++;
                }
                int pivot, start;
                KospiDailyPrice last = new KospiDailyPrice();
                if (idx < period) {
                    pivot = Integer.parseInt(arr[idx++].getStckClpr());
                    start = pivot;
                } else {
                    continue;
                }
                boolean isTrue = true;
                for (int i=idx; i<=period; i++) {
                    if (arr[idx] == null) continue;
                    if (pivot <= Integer.parseInt(arr[i].getStckClpr())) {
                        isTrue = false;
                        break;
                    }
                    pivot = Integer.parseInt(arr[i].getStckClpr());
                    last = arr[i];
                }
                if (isTrue) {
                    // 변화율 정보도 포함시키기
                    double totalCtrt =Math.round(((double)(pivot - start) / pivot)*100)/100.0;
                    double prdyCtrt = Math.round(((double)Integer.parseInt(last.getPrdyVrss()) / Integer.parseInt(last.getStckClpr()))*100)/100.0;
                    StockDto stockDto = new StockDto(
                            kospiItem.getMkscShrnIscd(),
                            kospiItem.getHtsKorIsnm(),
                            pivot,
                            kospiItem.getPrdyAvlsScal(),
                            prdyCtrt,
                            totalCtrt
                    );
                    result.add(stockDto);
                }
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private int getTimeDays(String date){
        int year = Integer.parseInt(date.substring(0, 4)) * 12 * 30;
        int month = Integer.parseInt(date.substring(4, 6)) * 30;
        int day = Integer.parseInt(date.substring(6, 8));
        return year + month + day;
    }

}
