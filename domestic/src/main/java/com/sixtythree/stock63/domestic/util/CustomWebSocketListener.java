package com.sixtythree.stock63.domestic.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import nonapi.io.github.classgraph.json.JSONUtils;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jakarta.websocket.Session;

import java.util.Map;

public class CustomWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private String json;
    private Session session;
    private Aes256 aes256;
    private String menuStr = "유가증권단축종목코드|주식체결시간|주식현재가|전일대비부호|전일대비|전일대비율|가중평균주식가격|주식시가|주식최고가|주식최저가|매도호가1|매수호가1|체결거래량|누적거래량|누적거래대금|매도체결건수|매수체결건수|순매수체결건수|체결강도|총매도수량|총매수수량|체결구분|매수비율|전일거래량대비등락율|시가시간|시가대비구분|시가대비|최고가시간|고가대비구분|고가대비|최저가시간|저가대비구분|저가대비|영업일자|신장운영구분코드|거래정지여부|매도호가잔량|매수호가잔량|총매도호가잔량|총매수호가잔량|거래량회전율|전일동시간누적거래량|전일동시간누적거래량비율|시간구분코드|임의종료구분코드|정적VI발동기준가";

    public CustomWebSocketListener(Session session) {
        this.session = session;
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
//        super.onClosed(webSocket, code, reason);
        System.out.printf("Socket Closed : %s / %s\r\n", code, reason);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
//        super.onClosing(webSocket, code, reason);
        System.out.printf("Socket Closing : %s / %s\n", code, reason);
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        webSocket.cancel();
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
//        super.onFailure(webSocket, t, response);
        System.out.println("Socket Error : " + t.getMessage());
    }

    @SneakyThrows
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
//        super.onMessage(webSocket, text);
//        System.out.println(text);
        if (text.isEmpty()) return;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode map = objectMapper.treeToValue(objectMapper.readTree(text), JsonNode.class);
        JsonNode body = map.get("body");

        // 230501 :: 웹소켓 응답 파싱 및 AES256 복호화 테스트코드
        // 장 운영시간에 테스트해야함..
        if (body.isEmpty()) {
            String[] arr = text.split("\\|");
            String[] data = arr[3].split("\\^");
            String[] menuArr = menuStr.split("\\|");
            for (int i = 0; i < menuArr.length; i++){
                System.out.println(menuArr[i] + " :::::: " + aes256.decrypt(data[i]));
            }
        } else {
            String iv = body.get("output").get("iv").toString();
            String key = body.get("output").get("key").toString();
            aes256 = new Aes256(key, iv);
        }
        session.getBasicRemote().sendText(text);
//        System.out.println(jsonNode.toPrettyString());
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        System.out.println("지원하지 않는 웹소켓 조회 유형입니다.");
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
//        super.onOpen(webSocket, response);
        webSocket.send(this.json);
    }

    public void setParameter(String json) {
        this.json = json;
    }
}
