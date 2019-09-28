package com.magneton.api2.builder.doc;

import com.sun.javadoc.ClassDoc;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
public interface ClassDocCollector {

    void collect(ApiClassDoc apiClass, ClassDoc classDoc);
}
