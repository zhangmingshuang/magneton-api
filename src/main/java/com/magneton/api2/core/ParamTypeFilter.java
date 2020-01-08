package com.magneton.api2.core;

import com.magneton.api2.builder.doc.ApiParamDoc;
import com.magneton.api2.builder.doc.ParamDocFilter;
import com.sun.javadoc.ParamTag;
import java.util.List;

/**
 * {@code @param的参数过滤器}
 *
 * @author zhangmingshuang
 * @since 2019/9/28
 */
public class ParamTypeFilter implements ParamDocFilter {

    /**
     * 要过滤的参数类型
     */
    private List<String> paramTypeFilters;

    public ParamTypeFilter(List<String> paramTypeFilters) {
        this.paramTypeFilters = paramTypeFilters;
    }

    @Override
    public boolean filter(ApiParamDoc apiParamDoc, ParamTag paramTag) {
        return this.paramTypeFilters.contains(apiParamDoc.getTypeName());
    }
}
