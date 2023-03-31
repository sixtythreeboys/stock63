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

    @Transactional
    public int inquireDailyItemChartPrice() throws Exception{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        String url = "https://openapivts.koreainvestment.com:29443/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice";
        String appkey = "PSM3WXIVMo4X2UnaIJCubQl4M9RCNfbm5C6V";
        String appsecret = "6J/t0za0MCCNCb74d0+/71iexBomHiT6NQJqx4YZandzS3k5Zb+gzgKdbyludx8xGnTzecmPpjspCteGLnGMVOnOIRpOCBV6Cqax4+xPkpj2rvk4NjNs8YR4PeGWoTb35T+wCnGYgalMOtoj1wcK4WDkg0XXA77jz+rE5qxULJbyA683TV8=";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b2tlbiIsImF1ZCI6IjAxZmU2MWIwLWI3ZmMtNGE0MC05YTVkLWFlMjM4NjhjMmZmZCIsImlzcyI6InVub2d3IiwiZXhwIjoxNjc5ODkyNDkyLCJpYXQiOjE2Nzk4MDYwOTIsImp0aSI6IlBTTTNXWElWTW80WDJVbmFJSkN1YlFsNE05UkNOZmJtNUM2ViJ9.BjsrpZuRA-3z087_cgzWrCK_jGBpO44BSt9SASc68L2FJg2dsAZdrg9i-H6nDgztMU9Zmt-mNC8QMeGrbPVULw");
        headers.add("appkey", appkey);
        headers.add("appsecret", appsecret);
        headers.add("tr_id", "FHKST03010100");
        headers.add("custtype", "P");
        HttpEntity<MultiValueMap<String, String>> request;
        ResponseEntity<Map> res;
        List<KospiItem> kospiItems = kospiItemRepository.findAll();
        int cnt = 0;
        System.out.println("---------------------"+ date + " 코스피 시작---------------------");
        for (KospiItem kospiItem : kospiItems) {
            cnt++;
            if (cnt == 20) {
                Thread.sleep(1000);
                cnt = 0;
            }
            UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                    .queryParam("FID_PERIOD_DIV_CODE", "D")
                    .queryParam("FID_ORG_ADJ_PRC", "0")
                    .queryParam("FID_INPUT_ISCD", kospiItem.getMkscShrnIscd())
                    .queryParam("FID_INPUT_DATE_1", date)
                    .queryParam("FID_INPUT_DATE_2", date)
                    .build(true);
            request = new HttpEntity<>(headers);
            res = restTemplate.exchange(
                    uriBuilder.toString(),
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            System.out.println("---------------------"+kospiItem.getHtsKorIsnm()+"---------------------" + kospiItem.getMkscShrnIscd());
            ArrayList<Map> kdpList = (ArrayList<Map>) res.getBody().get("output2");
            for (Map obj : kdpList) {
                KospiDailyPrice kdp = new KospiDailyPrice(obj);
                kdp.setMkscShrnIscd(kospiItem.getMkscShrnIscd());
                kospiDailyPriceRepository.save(kdp);
                System.out.println(kdp.getStckBsopDate() + " : " + kdp.getStckOprc());
            }
            System.out.println("---------------------"+kospiItem.getHtsKorIsnm()+" 완료---------------------" + kospiItem.getMkscShrnIscd());
        }
        System.out.println("---------------------"+ date + " 코스피 완료---------------------");
        return 1;
    }

    private int getTimeDays(String date){
        int year = Integer.parseInt(date.substring(0, 4)) * 12 * 30;
        int month = Integer.parseInt(date.substring(4, 6)) * 30;
        int day = Integer.parseInt(date.substring(6, 8));
        return year + month + day;
    }

}
