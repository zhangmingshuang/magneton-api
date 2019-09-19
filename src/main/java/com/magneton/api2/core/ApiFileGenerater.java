package com.magneton.api2.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/9/17
 */
@Setter
@Getter
public class ApiFileGenerater {
    /**
     * 生成环境包
     */
    private String envLib;
    private String folder;
    private List<ApiFile> apiFiles;
}
