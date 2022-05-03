package com.sxh.generator.dto;

import com.sxh.generator.domain.GenTable;
import com.sxh.generator.domain.GenTableColumn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 【修改代码生成业务】DTO
 *
 * @author JuniorRay
 * @date 2020-11-14
 */
@ApiModel(value = "GenTableInfoDTO", description = "修改代码生成业务DTO")
@Data
public class GenTableInfoDTO {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "业务表 gen_table")
    private GenTable info;

    @ApiModelProperty(value = "业务字段集合")
    private List<GenTableColumn> rows;

}
