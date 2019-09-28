package com.magneton.api2.generater;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhangmingshuang
 * @since 2019/9/17
 */
@Setter
@Getter
@ToString
public class ApiFile {
    private String fileName;
    private String content;

    private ApiFileGenerater apiFileEnvGenerater;
}
