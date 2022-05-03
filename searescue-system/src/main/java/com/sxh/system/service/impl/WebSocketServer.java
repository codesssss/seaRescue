package com.sxh.system.service.impl;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 服务实现类
 *
 * @author sxh
 * @date 2022/03/30 22:46
 **/

@Component
@ServerEndpoint("/push/websocket")
@Slf4j
@EqualsAndHashCode
public class WebSocketServer {
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private final static CopyOnWriteArraySet<WebSocketServer> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收sid
     */
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws IOException {
        this.session = session;
        // 加入set中
        WEB_SOCKET_SET.add(this);
        // 在线数加1
        addOnlineCount();
        log.info("有新客户端连接:" + sid + ",当前在线人数为" + getOnlineCount());
        this.sid = sid;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        WEB_SOCKET_SET.remove(this);
        // 在线数减1
        subOnlineCount();
        log.info("有一个客户端连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口" + sid + "的信息:" + message);
        System.out.println(session);
    }

    /**
     * @param session session
     * @param e       异常信息
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("发生错误");
        System.out.println(session);
        e.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public void sendInfo(String message) throws IOException {
        for (WebSocketServer item : WEB_SOCKET_SET) {
            item.sendMessage(message);
            log.info("推送信息内容=>{}", message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}
