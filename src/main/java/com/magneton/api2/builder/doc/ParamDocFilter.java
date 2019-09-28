package com.magneton.api2.builder.doc;

import com.sun.javadoc.ParamTag;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
public interface ParamDocFilter {

    boolean filter(ApiParamDoc apiParamDoc, ParamTag paramTag);
}
