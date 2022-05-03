package com.sxh.system.controller;

import com.sxh.system.service.impl.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sxh
 * @date 2022/03/31 00:40
 **/

@Slf4j
@RestController
@RequestMapping("/websocket")
public class WebSocketController {

    private final WebSocketServer webSocketServer;

    public WebSocketController (WebSocketServer webSocketServer) {
        this.webSocketServer= webSocketServer;
    }

    @GetMapping("/test")
    public void test() {
        try {
            webSocketServer.sendInfo("后端服务推送信息");
        } catch (Exception e) {
            log.error("分页查询数据接口列表失败", e);
        }
    }

}
