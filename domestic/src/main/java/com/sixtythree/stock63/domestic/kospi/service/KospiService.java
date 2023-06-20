package com.sixtythree.stock63.domestic.kospi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixtythree.stock63.domestic.kospi.dto.StockDto;
import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import com.sixtythree.stock63.domestic.kospi.entity.Token;
import com.sixtythree.stock63.domestic.kospi.repository.KospiDailyPriceRepository;
import com.sixtythree.stock63.domestic.kospi.repository.KospiItemRepository;
import com.sixtythree.stock63.domestic.kospi.repository.TokenRepository;
import com.sixtythree.stock63.domestic.util.CustomWebSocketListener;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KospiService {

    private final KospiItemRepository kospiItemRepository;
    private final KospiDailyPriceRepository kospiDailyPriceRepository;
    private final TokenRepository tokenRepository;

    String appkey = "PSM3WXIVMo4X2UnaIJCubQl4M9RCNfbm5C6V";
    String appsecret = "6J/t0za0MCCNCb74d0+/71iexBomHiT6NQJqx4YZandzS3k5Zb+gzgKdbyludx8xGnTzecmPpjspCteGLnGMVOnOIRpOCBV6Cqax4+xPkpj2rvk4NjNs8YR4PeGWoTb35T+wCnGYgalMOtoj1wcK4WDkg0XXA77jz+rE5qxULJbyA683TV8=";


    public ResponseEntity<List<StockDto>> stockList(int period, int avlsScal) {
        List<StockDto> result = new ArrayList<>();
        // 코스피 모든 종목 정보 불러오기
        List<KospiItem> kospiItems = kospiItemRepository.findAllOrderByPrdyAvlsScalDesc2();

        // startdate, enddate 정하기 -> 나중에 queryDSL로 짜보기..
        period = period >= 0 ? period + 1 : period - 1;
        int day = Math.abs(period);
        if (day == 1) day++;

        Map<String, String> dateMap  = kospiDailyPriceRepository.findStartEndDate(day);
        String startDate = dateMap.get("start_date");
        String endDate = dateMap.get("end_date");
        // 요청한 기간보다 더 작은 기간이 들어온 경우
        if (startDate == null
                || endDate == null
                || Integer.parseInt(endDate) - Integer.parseInt(startDate) + 1 < day) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        // 종목코드 키에 대한 데일리주가 객체 배열 값
        day = getTimeDays(endDate) - getTimeDays(startDate) + 1;
        Map<String, KospiDailyPrice[]> dailyInfoMap = new HashMap<>();
        for (KospiItem kospiItem : kospiItems) {
            dailyInfoMap.put(kospiItem.getMkscShrnIscd(), new KospiDailyPrice[day]);
        }

        // 30일 지난 데이터 삭제
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.DATE, -30);
//        String deleteDate = sdf.format(cal.getTime());
//        kospiDailyPriceRepository.deleteAllByStckBsopDate(deleteDate);



        // startDate부터 endDate까지 일별주식 가격select
        List<KospiDailyPrice> kdpList = kospiDailyPriceRepository.findAllByStckBsopDateBetween(startDate, endDate);
        for (KospiDailyPrice kdp : kdpList) {
            int diffsDay = getTimeDays(kdp.getStckBsopDate()) - getTimeDays(startDate);
            dailyInfoMap.get(kdp.getMkscShrnIscd())[diffsDay] = kdp;
        }
        //상승
        if (period > 1) {
            for (KospiItem kospiItem : kospiItems) {
                if ((avlsScal >= 0 && Integer.parseInt(kospiItem.getPrdyAvlsScal()) <= avlsScal)
                        || (avlsScal < 0 && Integer.parseInt(kospiItem.getPrdyAvlsScal()) > -avlsScal)) {
                    continue;
                }
                // 종목일별리스트를 불러온다.
                KospiDailyPrice[] arr = dailyInfoMap.get(kospiItem.getMkscShrnIscd());
                // 첫 기준값 설정 / 값이 비어있는경우(휴장 or 누락) 스킵한다.
                int idx = 0;
                while (idx < day && arr[idx] == null) {
                    idx++;
                }
                int pivot, start;
                KospiDailyPrice last = null;
                if (idx < day) {
                    pivot = Integer.parseInt(arr[idx++].getStckClpr());
                    start = pivot;
                } else {
                    continue;
                }
                // period만큼 연속 상승인지 확인. / 값이 비어있는 경우 스킵
                boolean isTrue = true;
                for (int i=idx; i<day; i++) {
                    if (arr[i] == null) continue;
                    // 연속 상승이 아닌경우 false
                    if (pivot >= Integer.parseInt(arr[i].getStckClpr())) {
                        isTrue = false;
                        break;
                    }
                    pivot = Integer.parseInt(arr[i].getStckClpr());
                    last = arr[i];
                }
                if (isTrue && last != null) {
                    double totalCtrt =Math.round(((double)(pivot - start) / pivot)*10000)/100.0;
                    double prdyCtrt = Math.round(((double)Integer.parseInt(last.getPrdyVrss()) / Integer.parseInt(last.getStckClpr()))*10000)/100.0;
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
        } else if (period < -1) {
            for (KospiItem kospiItem : kospiItems) {
                if ((avlsScal >= 0 && Integer.parseInt(kospiItem.getPrdyAvlsScal()) <= avlsScal)
                        || (avlsScal < 0 && Integer.parseInt(kospiItem.getPrdyAvlsScal()) >= -avlsScal)) {
                    continue;
                }
                // 휴장일은 모두 제외함.
                // period : 휴장일을 포함한 일수
                // period보다는 날짜 형식으로 요청을 받는게 좋을 것 같다.(휴장일을 모두 고려하기 어려움)
                KospiDailyPrice[] arr = dailyInfoMap.get(kospiItem.getMkscShrnIscd());
                int idx = 0;
                while (idx < day && arr[idx] == null) {
                    idx++;
                }
                int pivot, start;
                KospiDailyPrice last = null;
                if (idx < day) {
                    pivot = Integer.parseInt(arr[idx++].getStckClpr());
                    start = pivot;
                } else {
                    continue;
                }
                boolean isTrue = true;
                for (int i=idx; i<day; i++) {
                    if (arr[i] == null) continue;
                    if (pivot <= Integer.parseInt(arr[i].getStckClpr())) {
                        isTrue = false;
                        break;
                    }
                    pivot = Integer.parseInt(arr[i].getStckClpr());
                    last = arr[i];
                }
                if (isTrue && last != null) {
                    // 변화율 정보도 포함시키기
                    double totalCtrt =Math.round(((double)(pivot - start) / pivot)*10000)/100.0;
                    double prdyCtrt = Math.round(((double)Integer.parseInt(last.getPrdyVrss()) / Integer.parseInt(last.getStckClpr()))*10000)/100.0;
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
        } else if (period == 1) {
            // period 0
            for (KospiItem kospiItem : kospiItems) {
                if ((avlsScal >= 0 && Integer.parseInt(kospiItem.getPrdyAvlsScal()) <= avlsScal)
                        || (avlsScal < 0 && Integer.parseInt(kospiItem.getPrdyAvlsScal()) >= -avlsScal)) {
                    continue;
                }
                KospiDailyPrice[] arr = dailyInfoMap.get(kospiItem.getMkscShrnIscd());
                if (arr[0] == null || arr[day-1] == null) {
                    continue;
                }
                int start = Integer.parseInt(arr[0].getStckClpr());
                KospiDailyPrice last = arr[day-1];
                int pivot = Integer.parseInt(last.getStckClpr());
                double totalCtrt =Math.round(((double)(pivot - start) / pivot)*10000)/100.0;
                double prdyCtrt = Math.round(((double)Integer.parseInt(last.getPrdyVrss()) / Integer.parseInt(last.getStckClpr()))*10000)/100.0;
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
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity<List<KospiDailyPrice>> getPriceByPeriod(String mkscShrnIscd, String periodDivCode) throws ParseException {

        List<KospiDailyPrice> result = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String endDate = sdf.format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -3650);
        String startDate = sdf.format(cal.getTime());


        // token 가져오기
        Token token = tokenRepository.findById("access_token").orElse(null);
        // 만료된 토큰이면 업데이트 한다.
        Date nowDate = new Date();
        if (token == null || nowDate.compareTo(token.getExpired()) > 0) {
            token = updateToken();
        };


        //api요청 준비
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Map> res;
        // headeer, body setting
        ObjectNode jsonNodes = JsonNodeFactory.instance.objectNode();
        jsonNodes.put("grant_type","client_credentials");
        jsonNodes.put("appkey",appkey);
        jsonNodes.put("appsecret",appsecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JsonNode> requestToken = new HttpEntity<>(jsonNodes, headers);

        System.out.println(token.getValue());

        HttpEntity<MultiValueMap<String, String>> request;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token.getValue());
        headers.add("appkey", appkey);
        headers.add("appsecret", appsecret);
        headers.add("tr_id", "FHKST03010100");
        headers.add("custtype", "P");

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl("https://openapivts.koreainvestment.com:29443/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_PERIOD_DIV_CODE", periodDivCode)
                .queryParam("FID_ORG_ADJ_PRC", "0")
                .queryParam("FID_INPUT_ISCD", mkscShrnIscd)
                .queryParam("FID_INPUT_DATE_1", startDate)
                .queryParam("FID_INPUT_DATE_2", endDate)
                .build(true);
        request = new HttpEntity<>(headers);
        res = restTemplate.exchange(
                uriBuilder.toString(),
                HttpMethod.GET,
                request,
                Map.class
        );
        ArrayList<Map> kdpList = (ArrayList<Map>) res.getBody().get("output2");
        for (Map map : kdpList) {
            KospiDailyPrice kdp = new KospiDailyPrice(map);
            kdp.setMkscShrnIscd(mkscShrnIscd);
            result.add(kdp);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Token updateToken() throws ParseException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Map> res;
        ObjectNode jsonNodes = JsonNodeFactory.instance.objectNode();
        jsonNodes.put("grant_type","client_credentials");
        jsonNodes.put("appkey",appkey);
        jsonNodes.put("appsecret",appsecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JsonNode> requestToken = new HttpEntity<>(jsonNodes, headers);
        // 토큰 요청
        res = restTemplate.exchange(
                "https://openapivts.koreainvestment.com:29443/oauth2/tokenP",
                HttpMethod.POST,
                requestToken,
                Map.class
        );
        String value = (String) res.getBody().get("access_token");
        String expired = (String) res.getBody().get("access_token_token_expired");
        Token token = new Token();
        token.setTokenName("access_token");
        token.setValue(value);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        token.setExpired(formatter.parse(expired));
        token = tokenRepository.save(token);
        return token;
    }

    private int getTimeDays(String date){
        int year = Integer.parseInt(date.substring(0, 4)) * 12 * 30;
        int month = Integer.parseInt(date.substring(4, 6)) * 30;
        int day = Integer.parseInt(date.substring(6, 8));
        return year + month + day;
    }

}
