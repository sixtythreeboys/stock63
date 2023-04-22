package com.sixtythree.stock63.domestic.kospi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixtythree.stock63.domestic.kospi.dto.RealTimeInfo;
import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import com.sixtythree.stock63.domestic.util.CustomWebSocketListener;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(value = "/domestic/kospi/realtime")
@Service
public class WebSocketKospi {
    private static Set<Session> CLIENTS = Collections.synchronizedSet(new HashSet<>());
    String appkey = "PSM3WXIVMo4X2UnaIJCubQl4M9RCNfbm5C6V";
    String appsecret = "6J/t0za0MCCNCb74d0+/71iexBomHiT6NQJqx4YZandzS3k5Zb+gzgKdbyludx8xGnTzecmPpjspCteGLnGMVOnOIRpOCBV6Cqax4+xPkpj2rvk4NjNs8YR4PeGWoTb35T+wCnGYgalMOtoj1wcK4WDkg0XXA77jz+rE5qxULJbyA683TV8=";

    // 사용자가 접속하면 session을 추가합니다.
    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.toString());

        if (CLIENTS.contains(session)) {
            System.out.println("이미 연결된 세션입니다. > " + session);
        } else {
            CLIENTS.add(session);
            System.out.println("새로운 세션입니다. > " + session);
        }
    }

    // 사용자가 종료되면 session을 제거합니다.
    @OnClose
    public void onClose(Session session) throws Exception {
        CLIENTS.remove(session);
        System.out.println("세션을 닫습니다. : " + session);
    }

    // 사용자가 입력한 메세지를 받고 접속되어있는 사용자에게 메세지를 보냅니다.
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        System.out.println("입력된 메세지입니다. > " + message);
        RealTimeInfo realTimeInfo = new RealTimeInfo();
        realTimeInfo.setMkscShrnIscd(message);
        int stckPrpr = 15000;
        realTimeInfo.setStckPrpr(Integer.toString(stckPrpr));
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr;
        while (true) {
            Thread.sleep(2000);
            realTimeInfo.setStckPrpr(Integer.toString(stckPrpr));
            jsonStr = mapper.writeValueAsString(realTimeInfo);
            session.getBasicRemote().sendText(jsonStr);
            stckPrpr += 100;
        }

//        String mkscShrnIscd = "";
//
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0")
//                .build();
//
//        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
//        ObjectNode headerNode = JsonNodeFactory.instance.objectNode();
//        ObjectNode bodyNode = JsonNodeFactory.instance.objectNode();
//        ObjectNode inputNode = JsonNodeFactory.instance.objectNode();
//        inputNode.put("tr_id", "H0STCNT0");
//        inputNode.put("tr_key", mkscShrnIscd);
//        bodyNode.set("input", inputNode);
//        headerNode.put("appkey", appkey);
//        headerNode.put("appsecret", appsecret);
//        headerNode.put("custtype", "P");
//        headerNode.put("tr_type", "1");
//        headerNode.put("content-type", "utf-8");
//        jsonNode.set("header", headerNode);
//        jsonNode.set("body", bodyNode);
//
//        CustomWebSocketListener webSocketListener = new CustomWebSocketListener();
//        webSocketListener.setParameter(jsonNode.toString());
//
//        client.newWebSocket(request, webSocketListener);
//        client.dispatcher().executorService().shutdown();

    }
}
