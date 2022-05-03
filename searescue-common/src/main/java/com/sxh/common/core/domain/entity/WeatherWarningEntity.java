package com.sxh.common.core.domain.entity;

import com.sxh.common.core.domain.entity.WarningRefer;
import com.sxh.common.core.domain.entity.WeatherWarning;
import lombok.Data;

import java.util.List;

/**
 * @author sxh
 * @date 2022/04/06 22:45
 **/

@Data
public class WeatherWarningEntity {
    String code;
    String updateTime;
    String fxLink;
    List<WeatherWarning> warning;
    WarningRefer refer;
}

