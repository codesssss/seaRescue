package com.sxh.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author sxh
 * @since 2022-04-03
 */
@Getter
@Setter
@ApiModel(value = "MaydayInfo对象", description = "")
public class MaydayInfo implements Serializable {

    private Long id;

    private ShipEntity ship;

    private ShipEntity resship;

    private String info;

    private Integer status;

    private LocalDateTime createTime;


}
