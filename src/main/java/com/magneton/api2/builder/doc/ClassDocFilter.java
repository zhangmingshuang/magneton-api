package com.magneton.api2.builder.doc;

import com.sun.javadoc.ClassDoc;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
public interface ClassDocFilter {

    boolean filter(ApiClassDoc apiClass, ClassDoc classDoc);
}
