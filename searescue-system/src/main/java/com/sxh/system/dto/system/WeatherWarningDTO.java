package com.sxh.system.dto.system;

import com.sxh.common.core.domain.entity.WarningRefer;
import com.sxh.common.core.domain.entity.WeatherWarning;
import lombok.Data;

import java.util.List;

/**
 * @author sxh
 * @date 2022/04/06 22:45
 **/

@Data
public class WeatherWarningDTO {
    String code;
    String updateTime;
    String fxLink;
    List<WeatherWarning> warning;
    WarningRefer refer;
}
