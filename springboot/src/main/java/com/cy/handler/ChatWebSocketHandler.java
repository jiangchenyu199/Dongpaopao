package com.cy.handler;

import com.alibaba.fastjson2.JSONObject;
import com.cy.service.app.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天室 WebSocket 处理器
 */
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final MessageService messageService;

    // 存储在线用户的会话：key=userId, value=WebSocketSession
    private static final Map<String, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 处理收到的消息
        JSONObject jsonObject = JSONObject.parseObject(message.getPayload().toString());

        switch (jsonObject.getString("type")) {
            case "JOIN" -> handleJoinMessage(session, jsonObject);
            case "TEXT" -> handleTextMessage(session, jsonObject);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 处理加入聊天室消息
     */
    private void handleJoinMessage(WebSocketSession session, JSONObject jsonObject) {
        String userId = jsonObject.getString("userId");

        if (userId != null) {
            onlineUsers.put(userId, session);

            // 发送连接成功确认
            TextMessage response = new TextMessage(jsonObject.toJSONString());
            sendMessageToUser(userId, response);
        }
    }

    /**
     * 处理聊天消息
     */
    private void handleTextMessage(WebSocketSession session, JSONObject jsonObject) {
        /* 发送聊天消息 */
        messageService.sendMessage(jsonObject);
        jsonObject.put("status", "SENT");
        sendMessageToUser(jsonObject.getString("receiver"), new TextMessage(jsonObject.toJSONString()));
    }

    @SneakyThrows
    private void sendMessageToUser(String userId, WebSocketMessage message) {
        WebSocketSession session = onlineUsers.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(message);
        }
    }
}
