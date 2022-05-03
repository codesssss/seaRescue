package com.sxh.common.core.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 地图路径
 *
 * @author sxh
 * @date 2022/04/15 11:52
 **/
@Data
public class pathEntity {
    private List<BigDecimal> start;
    private List<BigDecimal> end;

    public pathEntity(List<BigDecimal> start, List<BigDecimal> end) {
        this.start = start;
        this.end = end;
    }
}
