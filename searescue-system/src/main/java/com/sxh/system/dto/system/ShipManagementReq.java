package com.sxh.system.dto.system;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author sxh
 */
@Data
public class ShipManagementReq {
    private Long id;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer capacity;

    private Integer maxspeed;

    private String type;

    private String name;

    private Integer load;

    private Integer isbusy;
}
