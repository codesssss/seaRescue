package com.sxh.system.dto.system;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author sxh
 */
@Data
public class LightHouseReq {
    private Long id;

    private BigDecimal longtitude;

    private BigDecimal latitude;

}
