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
@Accessors(chain = true)
@TableName("mayday")
@ApiModel(value = "MaydayEntity对象", description = "")
public class MaydayEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("shipid")
    private Long shipid;

    @TableField("info")
    private String info;

    @TableField("status")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField("resship")
    private Long resship;


}
