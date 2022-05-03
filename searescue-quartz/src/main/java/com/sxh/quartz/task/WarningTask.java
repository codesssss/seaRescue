package com.sxh.quartz.task;

import com.sxh.common.core.domain.entity.WeatherWarning;
import com.sxh.common.core.domain.entity.WeatherWarningEntity;
import com.sxh.system.domain.SysNotice;
import com.sxh.system.service.impl.SysNoticeServiceImpl;
import com.sxh.system.service.impl.WebSocketServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sxh
 * @date 2022/04/07 00:00
 **/

@Component("warningTask")
public class WarningTask {
    @Resource
    SysNoticeServiceImpl sysNoticeService;
    @Resource
    RestTemplate restTemplate;
    @Resource
    WebSocketServer webSocketServer;
    String url = "https://devapi.qweather.com/v7/warning/now?";
    String token = "0cd65d6f42af4f69bc06965f0e1b6b9f";

//    public executeWarningTask(){
//        for()
//    }

    public void getWarning(String location) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("key", token);
            map.put("location", location);
            ResponseEntity<WeatherWarningEntity> exchange = restTemplate.getForEntity(url, WeatherWarningEntity.class, map);
            HttpStatus statusCode = exchange.getStatusCode();
            int statusCodeValue = exchange.getStatusCodeValue();
            String storeNumber = exchange.toString();
            WeatherWarningEntity body = exchange.getBody();
            for (WeatherWarning item : body.getWarning()) {
                SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy H:mm");
                Date pubTime = format.parse(item.getPubTime());
                if (System.currentTimeMillis() - pubTime.getTime() >0) {
                    SysNotice sysNotice = new SysNotice();
                    sysNotice.setCreateTime(pubTime);
                    sysNotice.setNoticeTitle(item.getTitle());
                    sysNotice.setLevel(item.getLevel());
                    sysNotice.setNoticeType("3");
                    sysNotice.setWarningText(item.getText());
                    sysNotice.setWarningType(item.getTypeName());
                    sysNotice.setCreateBy("系统");
                    webSocketServer.sendInfo("有新的灾害预警，请查看通知界面");
                    sysNoticeService.insertNotice(sysNotice);
                }

            }
        } catch (Exception e) {

        }
    }
}
