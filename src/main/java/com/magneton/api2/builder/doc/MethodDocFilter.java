package com.magneton.api2.builder.doc;

import com.sun.javadoc.MethodDoc;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
public interface MethodDocFilter {

    boolean filter(ApiMethodDoc apiMethodDoc, MethodDoc methodDoc);
}
