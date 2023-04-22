package com.sixtythree.stock63.domestic.kospi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixtythree.stock63.domestic.kospi.dto.RealTimeInfo;
import com.sixtythree.stock63.domestic.kospi.dto.StockDto;
import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import com.sixtythree.stock63.domestic.kospi.entity.Token;
import com.sixtythree.stock63.domestic.kospi.repository.KospiDailyPriceRepository;
import com.sixtythree.stock63.domestic.kospi.repository.KospiItemRepository;
import com.sixtythree.stock63.domestic.util.CustomWebSocketListener;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KospiService {

    private final KospiItemRepository kospiItemRepository;
    private final KospiDailyPriceRepository kospiDailyPriceRepository;

    String appkey = "PSM3WXIVMo4X2UnaIJCubQl4M9RCNfbm5C6V";
    String appsecret = "6J/t0za0MCCNCb74d0+/71iexBomHiT6NQJqx4YZandzS3k5Zb+gzgKdbyludx8xGnTzecmPpjspCteGLnGMVOnOIRpOCBV6Cqax4+xPkpj2rvk4NjNs8YR4PeGWoTb35T+wCnGYgalMOtoj1wcK4WDkg0XXA77jz+rE5qxULJbyA683TV8=";


    public ResponseEntity<List<StockDto>> stockList(int period, int gradient) {

        // 코스피 모든 종목 정보 불러오기
        List<KospiItem> kospiItems = kospiItemRepository.findAllOrderByPrdyAvlsScalDesc2();
        // 종목코드 키에 대한 데일리주가 객체 배열 값
        Map<String, KospiDailyPrice[]> dailyInfoMap = new HashMap<>();
        for (KospiItem kospiItem : kospiItems) {
            dailyInfoMap.put(kospiItem.getMkscShrnIscd(), new KospiDailyPrice[period+1]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date());
        cal.add(Calendar.DATE, -30);
        String deleteDate = sdf.format(cal.getTime());
//        kospiDailyPriceRepository.deleteAllByStckBsopDate(deleteDate);


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
                KospiDailyPrice last = null;
                if (idx < period) {
                    pivot = Integer.parseInt(arr[idx++].getStckClpr());
                    start = pivot;
                } else {
                    continue;
                }
                // period만큼 연속 상승인지 확인. / 값이 비어있는 경우 스킵
                boolean isTrue = true;
                for (int i=idx; i<=period; i++) {
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
                KospiDailyPrice last = null;
                if (idx < period) {
                    pivot = Integer.parseInt(arr[idx++].getStckClpr());
                    start = pivot;
                } else {
                    continue;
                }
                boolean isTrue = true;
                for (int i=idx; i<=period; i++) {
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
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public RealTimeInfo getRealTimeInfo(String mkscShrnIscd){

        RealTimeInfo realTimeInfo = null;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0")
                .build();

        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        ObjectNode headerNode = JsonNodeFactory.instance.objectNode();
        ObjectNode bodyNode = JsonNodeFactory.instance.objectNode();
        ObjectNode inputNode = JsonNodeFactory.instance.objectNode();
        inputNode.put("tr_id", "H0STCNT0");
        inputNode.put("tr_key", mkscShrnIscd);
        bodyNode.set("input", inputNode);
        headerNode.put("appkey", appkey);
        headerNode.put("appsecret", appsecret);
        headerNode.put("custtype", "P");
        headerNode.put("tr_type", "1");
        headerNode.put("content-type", "utf-8");
        jsonNode.set("header", headerNode);
        jsonNode.set("body", bodyNode);

        CustomWebSocketListener webSocketListener = new CustomWebSocketListener();
        webSocketListener.setParameter(jsonNode.toString());

        client.newWebSocket(request, webSocketListener);
        client.dispatcher().executorService().shutdown();
        return null;
    }

//    private String getWebSocketKey(){
//        //api요청 준비
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        ResponseEntity<Map> res;
//        // headeer, body setting
//        ObjectNode jsonNodes = JsonNodeFactory.instance.objectNode();
//        jsonNodes.put("grant_type","client_credentials");
//        jsonNodes.put("appkey",appkey);
//        jsonNodes.put("secretkey",appsecret);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<JsonNode> requestToken = new HttpEntity<>(jsonNodes, headers);
//        // 토큰 요청
//        res = restTemplate.exchange(
//                "https://openapivts.koreainvestment.com:29443/oauth2/Approval",
//                HttpMethod.POST,
//                requestToken,
//                Map.class
//        );
//        String approvalKey = (String) res.getBody().get("approval_key");
//        return approvalKey;
//    }

    private int getTimeDays(String date){
        int year = Integer.parseInt(date.substring(0, 4)) * 12 * 30;
        int month = Integer.parseInt(date.substring(4, 6)) * 30;
        int day = Integer.parseInt(date.substring(6, 8));
        return year + month + day;
    }

}
