package com.sxh.quartz.task;

import com.google.common.collect.Maps;
import com.sxh.common.core.domain.entity.WeatherWarning;
import com.sxh.common.core.domain.entity.WeatherWarningEntity;
import com.sxh.system.domain.SysNotice;
import com.sxh.system.mapper.SysNoticeMapper;
import com.sxh.system.service.impl.SysNoticeServiceImpl;
import com.sxh.system.service.impl.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sxh
 * @date 2022/04/07 00:00
 **/

@Slf4j
@Component("warningTask")
@Service
public class WarningTask {
    @Resource
    SysNoticeServiceImpl sysNoticeService;
    @Resource
    SysNoticeMapper sysNoticeMapper;
    @Resource
    WebSocketServer webSocketServer;
    String url = "https://devapi.qweather.com/v7/warning/now";
    String token = "0cd65d6f42af4f69bc06965f0e1b6b9f";


    public void getWarning(String location) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            Map<String,String> param = Maps.newHashMap();
            param.put("key", token);
            param.put("location", location);
            param.entrySet().stream().forEach(o -> builder.queryParam(o.getKey(),o.getValue()));
            String url3 = builder.build().encode().toString();

            ResponseEntity<WeatherWarningEntity> exchange = getInstance("utf-8").getForEntity(url3, WeatherWarningEntity.class);
            HttpStatus statusCode = exchange.getStatusCode();
            int statusCodeValue = exchange.getStatusCodeValue();
            String storeNumber = exchange.toString();
            WeatherWarningEntity body = exchange.getBody();
            for (WeatherWarning item : body.getWarning()) {
                Map selectMap=new HashMap();
                selectMap.put("warning_id",item.getId());
                List<SysNotice> resultList=sysNoticeMapper.selectByMap(selectMap);
                if (resultList.size()==0) {
                    SysNotice sysNotice = new SysNotice();
                    sysNotice.setWarningId(Long.valueOf(item.getId()));
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
                log.error("请求错误",e);
        }
    }

    public static RestTemplate getInstance(String charset) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
//        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName(charset)));
        return restTemplate;
    }

}
