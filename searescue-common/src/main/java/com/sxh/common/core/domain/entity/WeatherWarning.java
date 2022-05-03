package com.sxh.common.core.domain.entity;

import lombok.Data;

/**
 * 气象预警
 *
 * @author sxh
 * @date 2022/04/06 22:33
 **/
@Data
public class WeatherWarning {
    String id;
    String sender;
    String pubTime;
    String title;
    String startTime;
    String endTime;
    String status;
    String level;
    String type;
    String typeName;
    String text;
    String related;
}
