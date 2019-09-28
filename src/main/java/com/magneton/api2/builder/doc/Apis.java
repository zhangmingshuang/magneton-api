package com.magneton.api2.builder.doc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 一个API容器
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
@Setter
@Getter
@ToString
public class Apis {

    /**
     * 错误码列表
     */
    private List<ApiFieldDoc> errorCodes;
    private List<ApiClassDoc> apiClasses;

    public boolean isEmpty() {
        return apiClasses == null || apiClasses.isEmpty();
    }
}
