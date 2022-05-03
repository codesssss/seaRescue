package com.sxh.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author sxh
 * @since 2022-02-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ship")
@ApiModel(value = "ShipEntity对象", description = "")
public class ShipEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("经度 ")
    @TableField("longitude")
    private BigDecimal longitude;

    @ApiModelProperty("纬度 ")
    @TableField("latitude")
    private BigDecimal latitude;

    @ApiModelProperty("容载量 ")
    @TableField("capacity")
    private Integer capacity;

    @ApiModelProperty("最大速度")
    @TableField("maxspeed")
    private Integer maxspeed;

    @ApiModelProperty("船只类型")
    @TableField("`type`")
    private String type;

    @ApiModelProperty("当前负载")
    @TableField("`load`")
    private Integer load;

    @TableField("isbusy")
    private Integer isbusy;

    @ApiModelProperty("船名 ")
    @TableField("name")
    private String name;
}
