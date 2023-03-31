package com.sixtythree.stock63.domestic.kospi.service;

import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import com.sixtythree.stock63.domestic.kospi.repository.KospiDailyPriceRepository;
import com.sixtythree.stock63.domestic.kospi.repository.KospiItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KospiService {

    private final KospiItemRepository kospiItemRepository;
    private final KospiDailyPriceRepository kospiDailyPriceRepository;

    public ResponseEntity<List<KospiItem>> stockList(int period, int gradient) {
        List<KospiItem> kospiItems = kospiItemRepository.findAllOrderByPrdyAvlsScalDesc2();
        Map<String, int[]> map = new HashMap<>();
        for (KospiItem kospiItem : kospiItems) {
            map.put(kospiItem.getMkscShrnIscd(), new int[period+1]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        String endDate = sdf.format(cal.getTime());
        cal.add(Calendar.DATE, -period);
        String startDate = sdf.format(cal.getTime());
        List<KospiDailyPrice> kdpList = kospiDailyPriceRepository.findAllByStckBsopDateBetween(startDate, endDate);
        for (KospiDailyPrice kdp : kdpList) {
            int diffsDay = getTimeDays(kdp.getStckBsopDate()) - getTimeDays(startDate);
            map.get(kdp.getMkscShrnIscd())[diffsDay] = Integer.parseInt(kdp.getStckOprc());
        }
        List<KospiItem> result = new ArrayList<>();
        if (gradient > 0) {
            for (KospiItem kospiItem : kospiItems) {
                int[] arr = map.get(kospiItem.getMkscShrnIscd());
                int idx = 0;
                while (idx <= period && arr[idx] == 0) {
                    idx++;
                }
                int pivot, start;
                if (idx < period) {
                    pivot = arr[idx++];
                    start = pivot;
                } else {
                    continue;
                }
                boolean isTrue = true;
                for (int i=idx; i<=period; i++) {
                    if (arr[i] == 0) continue;

                    if (pivot >= arr[i]) {
                        isTrue = false;
                        break;
                    }
                    pivot = arr[i];
                }
                if (isTrue) {
                    // 변화율 정보도 포함시키기
                    int delta = Math.abs(pivot - start);
                    result.add(kospiItem);
                }
            }
        } else if (gradient < 0) {
            for (KospiItem kospiItem : kospiItems) {
                // 휴장일은 모두 제외함.
                // period : 휴장일을 포함한 일수
                // period보다는 날짜 형식으로 요청을 받는게 좋을 것 같다.(휴장일을 모두 고려하기 어려움)
                int[] arr = map.get(kospiItem.getMkscShrnIscd());
                int idx = 0;
                while (idx <= period && arr[idx] == 0) {
                    idx++;
                }
                int pivot, start;
                if (idx < period) {
                    pivot = arr[idx++];
                    start = pivot;
                } else {
                    continue;
                }
                boolean isTrue = true;
                for (int i=idx; i<=period; i++) {
                    if (arr[i] == 0) continue;
                    if (pivot <= arr[i]) {
                        isTrue = false;
                        break;
                    }
                    pivot = arr[i];
                }
                if (isTrue) {
                    // 변화율 정보도 포함시키기
                    int delta = Math.abs(pivot - start);
                    result.add(kospiItem);
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
