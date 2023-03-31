package com.sixtythree.stock63.domestic.kospi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixtythree.stock63.domestic.kospi.entity.KospiDailyPrice;
import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import com.sixtythree.stock63.domestic.kospi.entity.Token;
import com.sixtythree.stock63.domestic.kospi.repository.KospiDailyPriceRepository;
import com.sixtythree.stock63.domestic.kospi.repository.KospiItemRepository;
import com.sixtythree.stock63.domestic.kospi.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KospiDailySchedulerService {

    private final KospiItemRepository kospiItemRepository;
    private final KospiDailyPriceRepository kospiDailyPriceRepository;
    private final TokenRepository tokenRepository;

    String appkey = "PSM3WXIVMo4X2UnaIJCubQl4M9RCNfbm5C6V";
    String appsecret = "6J/t0za0MCCNCb74d0+/71iexBomHiT6NQJqx4YZandzS3k5Zb+gzgKdbyludx8xGnTzecmPpjspCteGLnGMVOnOIRpOCBV6Cqax4+xPkpj2rvk4NjNs8YR4PeGWoTb35T+wCnGYgalMOtoj1wcK4WDkg0XXA77jz+rE5qxULJbyA683TV8=";

    @Scheduled(cron = "0 30 23 * * 1-5")
    public void run() throws InterruptedException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

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

        System.out.println(token.getValue());

        HttpEntity<MultiValueMap<String, String>> request;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token.getValue());
        headers.add("appkey", appkey);
        headers.add("appsecret", appsecret);
        headers.add("tr_id", "FHKST03010100");
        headers.add("custtype", "P");
        List<KospiItem> kospiItems = kospiItemRepository.findAll();
        int cnt = 0;
        System.out.println("---------------------"+ date + " 코스피 시작---------------------");
        for (KospiItem kospiItem : kospiItems) {
            cnt++;
            if (cnt == 19) {
                Thread.sleep(1000);
                cnt = 0;
            }
            UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl("https://openapivts.koreainvestment.com:29443/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
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
    }
}
