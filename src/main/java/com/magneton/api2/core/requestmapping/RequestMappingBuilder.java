package com.magneton.api2.core.requestmapping;

import com.magneton.api2.spi.Spi;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 * @author zhangmingshuang
 * @since 2019/9/19
 */
public interface RequestMappingBuilder extends Spi {

    String getRequestMapping(ClassDoc classDoc);

    String getRequestMethod(ClassDoc classDoc);

    String getRequestMethod(MethodDoc methodDoc);

    String getRequestMapping(MethodDoc methodDoc);
}
