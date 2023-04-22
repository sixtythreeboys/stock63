package com.sixtythree.stock63.domestic.util;

import com.fasterxml.jackson.databind.JsonNode;
import nonapi.io.github.classgraph.json.JSONUtils;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private String json;

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

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
//        super.onMessage(webSocket, text);
        System.out.println(text);
//        JsonNode jsonNode = JsonUtil.fromJson(text, JsonNode.class);
//        System.out.println(jsonNode.toPrettyString());
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        System.out.println("지원하지 않는 웹소켓 조회 유형입니다.");
//        super.onMessage(webSocket, bytes);
//        switch(siseType) {
//            case TRADE:
//                TradeResult tradeResult = JsonUtil.fromJson(bytes.string(StandardCharsets.UTF_8), TradeResult.class);
//                System.out.println(tradeResult);
//                break;
//            case TICKER:
//                TickerResult result = JsonUtil.fromJson(bytes.string(StandardCharsets.UTF_8), TickerResult.class);
//                System.out.println(result);
//                break;
//            case ORDERBOOK:
//                System.out.println(JsonUtil.fromJson(bytes.string(StandardCharsets.UTF_8), OrderBookResult.class));
//                break;
//            default:
//                throw new RuntimeException("지원하지 않는 웹소켓 조회 유형입니다. : " + siseType.getType());
//        }
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
