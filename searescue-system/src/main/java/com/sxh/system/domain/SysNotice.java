package com.sxh.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * éç¥å¬åè¡¨
 * </p>
 *
 * @author 
 * @since 2022-05-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_notice")
@ApiModel(value = "SysNoticeEntity对象", description = "éç¥å¬åè¡¨")
public class SysNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("å¬åID")
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Integer noticeId;

    @ApiModelProperty("å¬åæ é¢")
    @TableField("notice_title")
    private String noticeTitle;

    @ApiModelProperty("å¬åç±»åï¼1éç¥ 2å¬åï¼")
    @TableField("notice_type")
    private String noticeType;

    @ApiModelProperty("å¬ååå®¹")
    @TableField("notice_content")
    private Blob noticeContent;

    @ApiModelProperty("å¬åç¶æï¼0æ­£å¸¸ 1å³é­ï¼")
    @TableField("status")
    private String status;

    @ApiModelProperty("åå»ºè")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty("åå»ºæ¶é´")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("æ´æ°è")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty("æ´æ°æ¶é´")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty("å¤æ³¨")
    @TableField("remark")
    private String remark;

    @TableField("level")
    private String level;

    @TableField("warning_type")
    private String warningType;

    @TableField("warning_text")
    private String warningText;

    @TableField("warning_id")
    private Long warningId;

}
