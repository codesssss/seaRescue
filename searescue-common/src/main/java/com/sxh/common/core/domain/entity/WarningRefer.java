package com.sxh.common.core.domain.entity;

import lombok.Data;

import java.util.List;

/**
 * @author sxh
 * @date 2022/04/06 23:09
 **/

@Data
public class WarningRefer {
    List<String> sources;
    List<String> license;
}
