package com.magneton.api2.core;

import com.magneton.api2.builder.doc.ApiParamDoc;
import com.magneton.api2.builder.doc.ParamDocFilter;
import com.sun.javadoc.ParamTag;
import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
public class ParamFilter implements ParamDocFilter {

    private List<String> paramTypeFilters;

    public ParamFilter(List<String> paramTypeFilters) {
        this.paramTypeFilters = paramTypeFilters;
    }

    @Override
    public boolean filter(ApiParamDoc apiParamDoc, ParamTag paramTag) {
        return this.paramTypeFilters.contains(apiParamDoc.getTypeName());
    }
}
