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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KospiService {

    private final KospiItemRepository kospiItemRepository;
    private final KospiDailyPriceRepository kospiDailyPriceRepository;

    public ResponseEntity<List<KospiItem>> stockList(int period, int gradient) {
        List<KospiItem> kospiItems = kospiItemRepository.findTop100ByOrderByPrdyAvlsScalDesc();
        return new ResponseEntity<>(kospiItems, HttpStatus.OK);
    }

    @Transactional
    public int inquireDailyItemChartPrice() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        String url = "https://openapivts.koreainvestment.com:29443/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice";
        String appkey = "PSM3WXIVMo4X2UnaIJCubQl4M9RCNfbm5C6V";
        String appsecret = "6J/t0za0MCCNCb74d0+/71iexBomHiT6NQJqx4YZandzS3k5Zb+gzgKdbyludx8xGnTzecmPpjspCteGLnGMVOnOIRpOCBV6Cqax4+xPkpj2rvk4NjNs8YR4PeGWoTb35T+wCnGYgalMOtoj1wcK4WDkg0XXA77jz+rE5qxULJbyA683TV8=";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("accesstoken");
        headers.add("appkey", appkey);
        headers.add("appsecret", appsecret);
        headers.add("tr_id", "FHKST03010100");
        headers.add("custtype", "P");

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("FID_COND_MRKT_DIV_CODE", "J");
        map.add("FID_PERIOD_DIV_CODE", "D");
        map.add("FID_ORG_ADJ_PRC", "0");
        map.add("FID_INPUT_DATE_1", date);
        map.add("FID_INPUT_DATE_2", date);
        HttpEntity<MultiValueMap<String, String>> request;
        ResponseEntity<Result> res;
        List<KospiItem> kospiItems = kospiItemRepository.findAll();

        for (KospiItem kospiItem : kospiItems) {
            map.add("FID_INPUT_ISCD", kospiItem.getStndIscd());
            request = new HttpEntity<>(map, headers);
            res = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    Result.class
            );

            if (res.getStatusCode().is2xxSuccessful() && "0".equals(res.getBody().msg_cd)) {
                List<KospiDailyPrice> kdpList = res.getBody().output2;
                kospiDailyPriceRepository.saveAll(kdpList);
            } else {
                return -1;
            }
        }
        return 1;
    }

    static class Result {
        private String msg_cd;
        private Map<String, String> ouput1;
        private List<KospiDailyPrice> output2;
    }

}
