package com.sxh.system.service.impl;

import com.sxh.common.core.domain.entity.WeatherWarning;
import com.sxh.common.core.domain.entity.WeatherWarningEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sxh
 * @date 2022/04/04 20:15
 **/

@Slf4j
@Service
public class WebSocketServiceImpl {
    @Resource
    WebSocketServer webSocketServer;

    public void alertRescue() {
        try {
            webSocketServer.sendInfo("有新的求救信息！请查看救援管理界面！");
        } catch (Exception e) {
            log.error("分页查询数据接口列表失败", e);
        }
    }

    public void alertWarning(WeatherWarningEntity entity) {
        try {
            for (WeatherWarning item : entity.getWarning()) {
                webSocketServer.sendInfo(item.getTitle() + "\n"
                        + "灾害等级：" + item.getLevel() + "\n"
                        + "灾害类型：" + item.getTypeName() + "\n"
                        + "正文：" + item.getText() + "\n"
                        + "发起时间：" + item.getPubTime() + "\n"
                );
            }
        } catch (Exception e) {
            log.error("分页查询数据接口列表失败", e);
        }
    }
}
