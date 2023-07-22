package com.sixtythree.stock63.domestic.kospi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import com.sixtythree.stock63.domestic.kospi.entity.Token;
import com.sixtythree.stock63.domestic.kospi.repository.KospiItemRepository;
import com.sixtythree.stock63.domestic.kospi.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KospiItemSchedulerService {

    private final KospiItemRepository kospiItemRepository;
    private final TokenRepository tokenRepository;

    String appkey = "PSM3WXIVMo4X2UnaIJCubQl4M9RCNfbm5C6V";
    String appsecret = "6J/t0za0MCCNCb74d0+/71iexBomHiT6NQJqx4YZandzS3k5Zb+gzgKdbyludx8xGnTzecmPpjspCteGLnGMVOnOIRpOCBV6Cqax4+xPkpj2rvk4NjNs8YR4PeGWoTb35T+wCnGYgalMOtoj1wcK4WDkg0XXA77jz+rE5qxULJbyA683TV8=";

    @Scheduled(cron = "0 30 00 * * 1-5", zone = "Asia/Seoul")
    public void run() throws Exception {
        // token 가져오기
        Token token = tokenRepository.findById("access_token").orElse(null);
        // 만료된 토큰이면 업데이트 한다.
        // 동시에 많은 요청이 들어오는 경우 Update가 여러번 동작함 -> 보완 방법?
        Date nowDate = new Date();
        if (token == null || nowDate.compareTo(token.getExpired()) > 0) {
            token = updateToken();
        };

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token.getValue());
        headers.add("appkey", appkey);
        headers.add("appsecret", appsecret);
        headers.add("tr_id", "FHKST03010100");
        headers.add("custtype", "P");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        ResponseEntity<Map> res;
        List<KospiItem> kospiItems = kospiItemRepository.findAll();
        int cnt = 0;
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
            // api
            HttpEntity<JsonNode> requestToken = new HttpEntity<>(headers);
            res = restTemplate.exchange(
                    uriBuilder.toString(),
                    HttpMethod.GET,
                    requestToken,
                    Map.class
            );
            Map<String, String> dataMap =(Map) res.getBody().get("output1");
            if (dataMap == null) {
                kospiItemRepository.delete(kospiItem);
            } else {
                kospiItem.setPrdyAvlsScal(dataMap.get("hts_avls"));
                // kospiItem update
                kospiItemRepository.save(kospiItem);
            }
        }
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
}
